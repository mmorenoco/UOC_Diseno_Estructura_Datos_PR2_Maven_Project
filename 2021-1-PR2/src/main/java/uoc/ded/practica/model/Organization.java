package uoc.ded.practica.model;

import uoc.ei.tads.*;
import java.util.Comparator;

public class Organization implements Comparable<Organization> {
    public static final Comparator<Organization> CMP_V = (Organization o1, Organization o2)-> Integer.compare(o1.numActivities(), o2.numActivities());

    private String organizationId;
    private String description;
    private  String name;
    private Lista<Activity> activities;
    private Lista<Record> records;
    private Lista<Worker> workers;

    public Organization(String organizationId, String name, String description) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        activities = new ListaEncadenada<Activity>();
        records = new ListaEncadenada<Record>();
        workers = new ListaEncadenada<Worker>();
    }

    public String getName() {
        return name;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getDescription() {
        return description;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Iterador<Record> records() {
        return records.elementos();
    }

    public void addRecord(Record record) {
        records.insertarAlFinal(record);
    }

    public int numRecords() {
        return records.numElems();
    }

    public boolean hasRecords() {
        return records.numElems() > 0;
    }

    public Iterador<Worker> workers() {
        return workers.elementos();
    }

    public void addWorker(Worker worker) {
        workers.insertarAlFinal(worker);
    }

    public int numWorkers() {
        return workers.numElems();
    }

    public boolean hasWorkers() {
        return workers.numElems() > 0;
    }

    public void removeWorker(Worker worker) {
        workers.borrar(workerToBeDeleted(worker));
    }

    public Posicion<Worker> workerToBeDeleted(Worker worker) {
        Recorrido<Worker> workers = this.workers.posiciones();
        while(workers.haySiguiente()){
            Posicion<Worker> position = workers.siguiente();
            Worker workerToRemove = position.getElem();
            if(workerToRemove.equals(worker)){
                return position;
            }
        }
        return null;
    }

    public Iterador<Activity> activities() {
        return activities.elementos();
    }

    public void addActivity(Activity activity) {
        activities.insertarAlFinal(activity);
    }

    public int numActivities() {
        return activities.numElems();
    }

    public boolean hasActivities() {
        return activities.numElems() > 0;
    }

    @Override
    public int compareTo(Organization o) {
        return organizationId.compareTo(o.organizationId);
    }
}
