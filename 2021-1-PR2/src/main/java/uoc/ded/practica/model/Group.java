package uoc.ded.practica.model;

import uoc.ei.tads.Iterador;
import uoc.ei.tads.Lista;
import uoc.ei.tads.ListaEncadenada;
import java.time.LocalDate;

public class Group implements Comparable<Group> {
    private String groupId;
    private String description;
    private LocalDate date;
    private String[] members;
    private Lista<User> users;

    public Group(String groupId, String description, LocalDate date,String...members) {
        this.groupId = groupId;
        this.description =description;
        this.date = date;
        this.members = members;
        this.users = new ListaEncadenada<User>();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String[] getMembers(){
        return members;
    }

    public void setMembers(String[] members){
        this.members = members;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Iterador<User> users() {
        return users.elementos();
    }

    public void addUser(User user) {

        users.insertarAlFinal(user);
    }

    public int numMembers() {
        return users.numElems();
    }

    public boolean hasMembers() {
        return users.numElems() > 0;
    }

    public int compareTo(Group o){
        return groupId.compareTo(o.groupId);
    }
}
