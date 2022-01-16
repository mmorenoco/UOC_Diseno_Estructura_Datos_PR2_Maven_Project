package uoc.ded.practica;

import java.time.LocalDate;
import java.util.Date;
//import java.util.Iterator;
import uoc.ded.practica.exceptions.*;
import uoc.ded.practica.model.*;
//import uoc.ded.practica.util.DiccionarioOrderedVector;
import uoc.ded.practica.util.OrderedVector;
import uoc.ei.tads.*;

public class SafetyActivities4Covid19Impl implements SafetyActivities4Covid19 {

    //Tabla dispersion de usuarios
    private Diccionario<String, User> users;
    //Tabla dispersion de organizaciones
    private Diccionario<String, Organization> organizations;
    //Lista encadenada trabajadores
    private Lista<Worker> workers;
    //Cola con prioridad de expedientes
    private ColaConPrioridad<Record> records;
    //DiccionarioAVL actividades
    private Diccionario<String, Activity> activities;
    //Vector de java de roles
    private Role[] roles;
    private int numRoles;
    //AVL de grupos
    private DiccionarioAVLImpl<String, Group> groups;
    //AVL órdenes de compra
    private DiccionarioAVLImpl<String, Order> orders;
    //records pendientes y rechazados
    private int totalRecords;
    private int rejectedRecords;
    //Usuario mas activo
    private User mostActiveUser;
    //Vector ordenado de actividades mejor valoradas
    private OrderedVector<Activity> best10Activities;
    //Vector ordenado de organizaciones mejor valoradas
    private OrderedVector<Organization> best5Organizations;

    public SafetyActivities4Covid19Impl() {
        users = new TablaDispersion<String, User>();
        organizations = new TablaDispersion<String, Organization>();
        records = new ColaConPrioridad<>();
        activities = new DiccionarioAVLImpl<String,Activity>();
        roles = new Role[R];
        numRoles = 0;
        groups = new DiccionarioAVLImpl<String, Group>();
        orders = new DiccionarioAVLImpl<String, Order>();
        totalRecords = 0;
        rejectedRecords = 0;
        workers = new ListaEncadenada<Worker>();
        mostActiveUser = null;
        best10Activities = new OrderedVector<Activity>(BEST_10_ACTIVITIES, Activity.CMP_V);
        best5Organizations = new OrderedVector<Organization>(BEST_ORGANIZATIONS, Organization.CMP_V);
    }

    public void addUser(String userId, String name, String surname, LocalDate birthday, boolean covidCertificate) {
        User user = getUser(userId);
        if (user != null) {
            user.setName(name);
            user.setSurname(surname);
            user.setBirthday(birthday);
            user.setCovidCertificate(covidCertificate);
        } else {
            user = new User(userId, name, surname, birthday, covidCertificate);
            this.users.insertar(userId,user);
        }
    }

    public User getUser(String userId) {
        return this.users.consultar(userId);
    }

    public void addOrganization(String organizationId, String name, String description) {
        Organization organization = getOrganization(organizationId);
        if (organization != null) {
            organization.setName(name);
            organization.setDescription(description);
        } else {
            organization = new Organization(organizationId, name, description);
            this.organizations.insertar(organizationId, organization);
        }
    }

    public Organization getOrganization(String organizationId) {
        return this.organizations.consultar(organizationId);
    }

    public void addRecord(String recordId, String actId, String description, Date date, LocalDate dateRecord,Mode mode, int num, String organizationId) throws OrganizationNotFoundException{
        Organization organization = getOrganization(organizationId);
        if (organization == null) {
        	throw new OrganizationNotFoundException();
        }

        Record record = new Record(recordId, actId, description, date, dateRecord, mode, num, organization);
        this.records.encolar(record);
        organization.addRecord(record);
        totalRecords++;
    }

    public Record updateRecord(Status status, Date date, String description) throws NoRecordsException {
        Record record = records.desencolar();
        if (record  == null) {
        	throw new NoRecordsException();
        }

        record.update(status, date, description);
        if (record.isEnabled()) {
            Activity activity = record.newActivity();
            activities.insertar(activity.getActId(), activity);

        }
        else {
        	rejectedRecords++;
        }

        return record;
    }

    public Record currentRecord() {
        return (records.numElems() > 0 ? records.primero() : null);
    }

    public Iterador<Activity> getActivitiesByUser(String userId) throws NoActivitiesException {
        User user = getUser(userId);

        if (!user.hasActivities()) {
        	throw new NoActivitiesException();
        }
        return user.activities();
    }

    public Iterador<Activity> getAllActivities() throws NoActivitiesException {
        if (activities.numElems() == 0) {
        	throw new NoActivitiesException();
        }
        return activities.elementos();
    }

    public double getInfoRejectedRecords() {
        return (double)rejectedRecords / totalRecords;
    }

    public Order createTicket(String userId, String actId, LocalDate date) throws UserNotFoundException,
            ActivityNotFoundException, LimitExceededException {

        User user = getUser(userId);
        if (user == null) {
        	throw new UserNotFoundException();
        }

        Activity activity = getActivity(actId);
        if (activity  == null) {
        	throw new ActivityNotFoundException();
        }

        if (!activity.hasAvailabilityOfTickets()) {
        	throw new LimitExceededException();
        }

        double value = user.getBadge(date).getValue();
        String orderCode = "O-".concat(date.toString().replace("-","")).concat("-").concat(userId);
        user.addActivity(activity);
        updateMostActiveUser(user);
        Ticket ticket = new Ticket(user,activity);
        Order order = new Order(orderCode,actId,date);
        order.setValue(value);
        order.addTicket(ticket);
        activity.addOrder(order);
        activity.addUser(user);
        this.orders.insertar(order.getId(), order);
        return order;
    }


    public Order assignSeat(String actId) throws ActivityNotFoundException {

        Activity activity = getActivity(actId);
        if (activity == null) {
        	throw new ActivityNotFoundException();
        }

        return activity.popOrder();
    }

    public void addRating(String actId, Rating rating, String message, String userId)
            throws ActivityNotFoundException, UserNotFoundException, UserNotInActivityException {
        Activity activity = getActivity(actId);
        if (activity == null) {
        	throw new ActivityNotFoundException();
        }

        User user = getUser(userId);
        if (user == null) {
        	throw new UserNotFoundException();
        }

        if (!user.isInActivity(actId)) {
        	throw new UserNotInActivityException();
        }

        activity.addRating(rating, message, user);
        updateBestActivity(activity);

    }

    private void updateBestActivity(Activity activity) {
        best10Activities.delete(activity);
        best10Activities.update(activity);
    }

    public Iterador<uoc.ded.practica.model.Rating> getRatings(String actId) throws ActivityNotFoundException, NoRatingsException {
        Activity activity = getActivity(actId);
        if (activity  == null) {
        	throw new ActivityNotFoundException();
        }

        if (!activity.hasRatings()) {
        	throw new NoRatingsException();
        }

        return activity.ratings();
    }


    private void updateMostActiveUser(User user) {
        if (mostActiveUser == null) {
            mostActiveUser = user;
        }
        else if (user.numActivities() > mostActiveUser.numActivities()) {
            mostActiveUser = user;
        }
    }


    public User mostActiveUser() throws UserNotFoundException {
        if (mostActiveUser == null) {
        	throw new UserNotFoundException();
        }

        return mostActiveUser;
    }

    public Activity bestActivity() throws ActivityNotFoundException {
        if (best10Activities.numElems() == 0) {
        	throw new ActivityNotFoundException();
        }

        return best10Activities.elementAt(0);
    }

    public int numUsers() {
        return this.users.numElems();
    }

    public int numOrganizations() {
        return this.organizations.numElems();
    }

    public int numPendingRecords() {
        return records.numElems();
    }

    public int numRecords() {
        return totalRecords;
    }

    public int numRejectedRecords() {
        return rejectedRecords;
    }

    public int numActivities() {
        return activities.numElems();
    }

    public int numActivitiesByOrganization(String organizationId) {
        Organization organization = getOrganization(organizationId);

        return organization.numActivities();
    }

    public int numRecordsByOrganization(String organizationId) {
        Organization organization = getOrganization(organizationId);

        return organization.numRecords();
    }

    public Activity getActivity(String actId) {
        return activities.consultar(actId);
    }


    public int availabilityOfTickets(String actId) {
        Activity activity = getActivity(actId);

        return (activity != null ? activity.availabilityOfTickets() : 0);
    }


    //PR2

    public void addRole(String roleId, String name){
        Role role = getRole(roleId);
        if(role != null){
            role.setName(name);
        } else  {
            role = new Role(roleId,name);
            addRole(role);
        }
    }

    public void addRole(Role role){
        roles[numRoles++] = role;
    }

    public void addWorker(String userId, String name, String surname, LocalDate birthday,
                          boolean covidCertificate, String roleId, String organizationId){
        User user = getUser(userId);
        if(user != null) {
            user.setName(name);
            user.setSurname(surname);
            user.setBirthday(birthday);
            user.setCovidCertificate(covidCertificate);
            Worker worker = getWorker(userId);
            if(worker == null) {
                worker = new Worker(userId,name,surname,birthday,covidCertificate,roleId,organizationId);
                this.workers.insertarAlFinal(worker);
                Role role = getRole(roleId);
                if(role != null){
                    role.addWorker(worker);
                }
                Organization organization = getOrganization(organizationId);
                if(organization != null){
                    organization.addWorker(worker);
                }
            } else {
                worker.setName(name);
                worker.setSurname(surname);
                worker.setBirthday(birthday);
                worker.setCovidCertificate(covidCertificate);

                Role oldRole = getRole(worker.getRoleId());
                if(!(roleId.equals(oldRole.getRoleId()))){
                    //Delete worker from role
                    oldRole.removeWorker(worker);
                    worker.setRoleId(roleId);
                    //Add worker to new role
                    Role newRole = getRole(roleId);
                    newRole.addWorker(worker);
                }

                Organization oldOrganization = getOrganization(worker.getOrganizationId());
                if(!(organizationId.equals(oldOrganization.getOrganizationId()))){
                    //Delete worker from role
                    oldOrganization.removeWorker(worker);
                    worker.setOrganizationId(organizationId);
                    //Add worker to new role
                    Organization newOrganization = getOrganization(organizationId);
                    newOrganization.addWorker(worker);
                }
            }
        } else {
            user = new User(userId,name,surname,birthday,covidCertificate);
            this.users.insertar(userId,user);
            Worker worker = new Worker(userId,name,surname,birthday,covidCertificate,roleId,organizationId);
            this.workers.insertarAlFinal(worker);
            Role role = getRole(roleId);
            if(role != null){
                role.addWorker(worker);
            }
            Organization organization = getOrganization(organizationId);
            if(organization != null){
                organization.addWorker(worker);
            }
        }
    }

    public Iterador<Worker> getWorkersByOrganization(String organizationId) throws OrganizationNotFoundException, NoWorkersException {
        Organization organization = getOrganization(organizationId);
        if(organization == null){
            throw new OrganizationNotFoundException();
        }

        if(!organization.hasWorkers()){
            throw new NoWorkersException();
        }

        return organization.workers();
    }

    public Iterador<User> getUsersInActivity(String activityId) throws ActivityNotFoundException, NoUserException {
        Activity activity = getActivity(activityId);

        if(activity == null) {
            throw new ActivityNotFoundException();
        }

        if(!(activity.hasUsers())) {
            throw new NoUserException();
        }

        return activity.users();
    }

    public Badge getBadge(String userId, LocalDate day) throws UserNotFoundException {
        User user = getUser(userId);
        Badge badge;

        if(user == null) {
            throw new UserNotFoundException();
        } else {
            badge = user.getBadge(day);
        }
        return badge;
    }

    public void addGroup(String groupId, String description, LocalDate date, String... members ) {
        Group group = getGroup(groupId);

        if(group != null) {
            group.setDescription(description);
            group.setMembers(members);
        } else {
            group = new Group(groupId,description,date,members);
            for(String member: members){
                group.addUser(getUser(member));
            }
            this.groups.insertar(groupId,group);
        }
    }

    public Iterador<User> membersOf(String groupId) throws GroupNotFoundException, NoUserException {
        Group group = getGroup(groupId);

        if(group == null){
            throw new GroupNotFoundException();
        }

        if(!(group.hasMembers())){
            throw new NoUserException();
        }

        return group.users();

    }

    public double valueOf(String groupId) throws GroupNotFoundException {
        Group group = getGroup(groupId);
        double value = 0;

        if(group == null){
            throw new GroupNotFoundException();
        } else {
            String[] members = group.getMembers();
            for(int i= 0; i < members.length; i++){
                String userId = members[i];
                User user = getUser(userId);
                value = user.getBadge(LocalDate.now()).getValue() + value;
            }
            return (value/members.length);
        }
    }

    public Order createTicketByGroup(String groupId, String actId, LocalDate date) throws GroupNotFoundException,
            ActivityNotFoundException, LimitExceededException {
        Group group = getGroup(groupId);
        if(group == null){
            throw new GroupNotFoundException();
        }

        Activity activity = getActivity(actId);
        if(activity == null){
            throw new ActivityNotFoundException();
        }

        if(!(activity.hasAvailabilityOfTickets())) {
            throw new LimitExceededException();
        }

        double valueGroup = 0;
        double valueUser = 0;
        String[] members = group.getMembers();
        for(String member: members){
            User user = getUser(member);
            valueUser += user.getBadge(date).getValue();
        }

        valueGroup = valueUser/group.numMembers();
        String orderCode = "O-".concat(date.toString().replace("-","")).concat("-").concat(groupId);
        Order order = new Order(orderCode,actId,date);
        order.setValue(valueGroup);
        for(String member: members){
            User user = getUser(member);
            Ticket ticket = new Ticket(user,activity);
            order.addTicket(ticket);
            activity.addUser(user);
        }
        activity.addOrder(order);
        this.orders.insertar(order.getId(), order);
        return order;
    }

    public Order getOrder (String orderId) throws OrderNotFoundException{
        if(!this.orders.esta(orderId)){
            throw new OrderNotFoundException();
        }

        return this.orders.consultar(orderId);
    }

    public Iterador<Worker> getWorkersByRole(String roleId) throws NoWorkersException {

        Role role = getRole(roleId);

        if(!role.hasWorkers()){
            throw new NoWorkersException();
        }

        return role.workers();
    }

    public Iterador<Activity> getActivitiesByOrganization(String organizationId) throws NoActivitiesException {
        Organization organization = getOrganization(organizationId);

        if(!organization.hasActivities()){
            throw new NoActivitiesException();
        }

        return organization.activities();
    }

    public Iterador<Record> getRecordsByOrganization(String organizationId) throws NoRecordsException {
        Organization organization = getOrganization(organizationId);

        if(!organization.hasRecords()){
            throw new NoRecordsException();
        }

        return organization.records();
    }

    public Iterador<Organization> best5Organizations() throws NoOrganizationException {
        if(organizations.numElems() == 0){
            throw new NoOrganizationException();
        }
        return this.best5Organizations.elementos();
    }

    public Worker getWorker(String workerId){
        Iterador<Worker> workers = this.workers.elementos();
        while(workers.haySiguiente()){
            Worker worker = workers.siguiente();
            if(worker.getUserId().equals(workerId)){
                return worker;
            }
        }
        return null;
    }

    public Role getRole(String roleId) {
        for(Role role: roles){
            if(role == null){
                return null;
            } else if(role.is(roleId)){
                return role;
            }
        }
        return null;
    }

    public Group getGroup(String groupId) {
        return this.groups.consultar(groupId);
    }

    public int numWorkers() {
        return workers.numElems();
    }

    public int numWorkers(String organizationId) {
        Organization organization = getOrganization(organizationId);

        return organization.numWorkers();
    }

    public int numRoles(){
        return this.numRoles;
    }

    public int numWorkersByRole(String roleId) {
        Role role = getRole(roleId);

        return role.numWorkers();
    }

    public int numGroups() {
        return this.groups.numElems();
    }

    public int numOrders() {
        return this.orders.numElems();
    }

    public Iterador<Activity> best10Activities() throws ActivityNotFoundException {
        if(activities.numElems() == 0){
            throw new ActivityNotFoundException();
        }
        return this.best10Activities.elementos();
    }

}
