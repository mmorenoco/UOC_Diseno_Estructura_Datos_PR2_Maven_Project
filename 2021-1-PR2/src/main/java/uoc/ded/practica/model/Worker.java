package uoc.ded.practica.model;

import java.time.LocalDate;

public class Worker {

    private String userId;
    private String name;
    private String surname;
    private LocalDate birthday;
    private boolean covidCertificate;
    private String roleId;
    private String organizationId;

    public Worker(String userId, String name, String surname,LocalDate birthday, boolean covidCertificate,String roleId,String organizationId) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.birthday = birthday;
        this.covidCertificate = covidCertificate;
        this.roleId = roleId;
        this.organizationId = organizationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public boolean isCovidCertificate() {
        return covidCertificate;
    }

    public void setCovidCertificate(boolean covidCertificate) {
        this.covidCertificate = covidCertificate;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
