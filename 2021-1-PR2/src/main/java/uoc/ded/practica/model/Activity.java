package uoc.ded.practica.model;

import java.util.Comparator;
import java.util.Date;

import uoc.ded.practica.SafetyActivities4Covid19;
import uoc.ei.tads.Cola;
import uoc.ei.tads.ColaConPrioridad;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;

public class Activity implements Comparable<Activity> {
    public static final Comparator<Activity> CMP_V = (Activity a1, Activity a2)-> Double.compare(a1.rating(), a2.rating());

    private String actId;
    private String description;
    private Date date;
    private SafetyActivities4Covid19.Mode mode;
    private int total;
    private int nextSeat;
    private int availabilityOfTickets;
    private Record record;
    private Cola<Order> orders;
    private Lista<Rating> ratings;
    private Lista<User> users;
    private int totalRatings;

    public Activity(String actId, String description, Date dateAct, SafetyActivities4Covid19.Mode mode, int num, Record record) {

        this.actId = actId;
        this.description = description;
        this.date = dateAct;
        this.mode = mode;
        this.total = num;
        this.nextSeat = 1;
        this.availabilityOfTickets = num;
        this.record = record;
        orders = new ColaConPrioridad<Order>();
        ratings = new ListaEncadenada<Rating>();
        users = new ListaEncadenada<User>();
    }

    public String getActId() {
        return actId;
    }

    public boolean hasAvailabilityOfTickets() {
        return (availabilityOfTickets > 0  );
    }

    public void addOrder(Order order){
        this.orders.encolar(order);
    }

    public Order popOrder(){
        Order order = orders.desencolar();
        Iterador<Ticket> it = order.tickets();
        while(it.haySiguiente()){
            Ticket ticket = it.siguiente();
            ticket.setSeat(nextSeat++);
        }
        return order;
    }

    public boolean is(String actId) {
        return this.actId.equals(actId);
    }

    public void addRating(SafetyActivities4Covid19.Rating rating, String message, User user) {
        Rating newRating = new Rating(rating, message, user);
        ratings.insertarAlFinal(newRating);
        totalRatings += rating.getValue();
    }

    public double rating() {
        return (ratings.numElems() != 0 ? (double)totalRatings / ratings.numElems() : 0);
    }

    public Iterador<Rating> ratings() {
        return ratings.elementos();
    }

    public boolean hasRatings() {
        return ratings.numElems() > 0;
    }

    public int availabilityOfTickets() {
        return availabilityOfTickets;
    }

    public Iterador<User> users() {
        return users.elementos();
    }

    public void addUser(User user) {
        users.insertarAlFinal(user);
    }

    public int numUsers() {
        return users.numElems();
    }

    public boolean hasUsers() {
        return users.numElems() > 0;
    }

    public Record getRecord() {
        return record;
    }
    public void setRecord(Record record) {
        this.record = record;
    }

    @Override
    public int compareTo(Activity o) {
        return actId.compareTo(o.actId);
    }

    public void setAvailableTicket() {
        availabilityOfTickets--;
    }
}
