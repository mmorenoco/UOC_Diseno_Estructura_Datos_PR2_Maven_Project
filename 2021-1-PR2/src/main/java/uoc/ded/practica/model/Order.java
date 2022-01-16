package uoc.ded.practica.model;

import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;
import java.time.LocalDate;

public class Order implements Comparable<Order> {
    private String id;
    private String actId;
    private LocalDate date;
    private double value;
    private Lista<Ticket> tickets;

    public Order(String id, String actId, LocalDate date){
        this.id = id;
        this.actId = actId;
        this.date = date;
        this.value = 0;
        this.tickets = new ListaEncadenada<Ticket>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getValue(){
        return value;
    }

    public void setValue(double value){
        this.value = value;
    }

    public Iterador<Ticket> tickets() {
        return tickets.elementos();
    }

    public void addTicket(Ticket ticket) {
        Activity activity = ticket.getActivity();
        tickets.insertarAlFinal(ticket);
        activity.setAvailableTicket();
    }

    public int numTickets() {
        return tickets.numElems();
    }

    public boolean hasTickets() {
        return tickets.numElems() > 0;
    }

    @Override
    public int compareTo(Order o) {
        int ret = 0;
        Double ord1 = this.getValue();
        Double ord2 = o.getValue();

        ret = ord2.compareTo(ord1);

        return ret;
    }
}
