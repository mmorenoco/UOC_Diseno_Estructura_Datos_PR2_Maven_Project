package uoc.ded.practica.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import uoc.ded.practica.SafetyActivities4Covid19.Badge;
import uoc.ded.practica.SafetyActivities4Covid19;
import uoc.ei.tads.*;

public class User implements Comparable<User>{
    public static final Comparator<String> CMP = new Comparator<String>() {
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    private String id;
    private String name;
    private String surname;
    private LocalDate birthday; // No necesitamos la hora de nacimiento, LocalDate no lleva hora
    private boolean covidCertificate;
    private Lista<Activity> activities;

	public User(String idUser, String name, String surname, LocalDate birthday, boolean covidCertificate) {
        this.setId(idUser);
        this.setName(name);
        this.setSurname(surname);
        this.setBirthday(birthday);
        this.setCovidCertificate(covidCertificate);
        this.activities = new ListaEncadenada<Activity>();
    }

    public Badge getBadge(LocalDate date) {
        Period interval = Period.between(birthday, date);
        int age = interval.getYears();

        if(age >= 65 && this.covidCertificate) {
            return Badge.SENIOR_PLUS;
        }
        if(age >= 50 && age <= 64 && this.covidCertificate){
            return Badge.SENIOR;
        }
        if(age >= 30 && age <= 49 && this.covidCertificate && activities.numElems() >5){
            return Badge.MASTER_PLUS;
        }
        if(age >= 18 && age <= 29 && this.covidCertificate && activities.numElems() > 10) {
            return Badge.YOUTH_PLUS;
        }
        if(age >= 30 && age <= 49 && this.covidCertificate && activities.numElems() < 5){
            return Badge.MASTER;
        }
        if(age >= 18 && age <= 29 && this.covidCertificate && activities.numElems() < 5) {
            return Badge.YOUTH;
        }
        if(age >= 12 && age <= 17 && this.covidCertificate){
            return Badge.JUNIOR_PLUS;
        }
        if(age < 12){
            return Badge.JUNIOR;
        }
        if(age > 12 && !this.covidCertificate){
            return Badge.DARK;
        }

        return Badge.DARK;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public boolean isCovidCertificate() {
		return covidCertificate;
	}

    public int compareTo(User o) {
        return getId().compareTo(o.getId());
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setCovidCertificate(boolean covidCertificate) {
        this.covidCertificate = covidCertificate;
    }

    public boolean is(String userId) {
        return id.equals(userId);
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

    public boolean isInActivity(String actId) {
        Iterador<Activity> it = activities.elementos();

        boolean found = false;
        Activity act = null;

        while (!found && it.haySiguiente()) {
        	act = it.siguiente();
            found = act.is(actId);
        }

        return found;
    }

    public boolean hasActivities() {
        return activities.numElems() > 0;
    }

}
