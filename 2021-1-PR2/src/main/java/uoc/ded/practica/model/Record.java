package uoc.ded.practica.model;

import uoc.ded.practica.SafetyActivities4Covid19;
import java.time.LocalDate;
import java.util.Date;

public class Record implements Comparable<Record>{

    private String recordId;
    private String actId;
    private String description;
    private Date dateAct;
    private LocalDate dateStatus;
    private Date reviseRecord;
    private String descriptionStatus;
    private SafetyActivities4Covid19.Mode mode;
    private int num;
    private SafetyActivities4Covid19.Status status;
    private Organization organization;
    private Activity activity;


    public Record(String recordId, String actId, String description, Date dateAct, LocalDate dateStatus,
                  SafetyActivities4Covid19.Mode mode, int num, Organization organization) {
        this.recordId = recordId;
        this.actId = actId;
        this.description = description;
        this.dateAct = dateAct;
        this.dateStatus = dateStatus;
        this.mode = mode;
        this.num = num;
        this.status = SafetyActivities4Covid19.Status.PENDING;
        this.organization = organization;
        this.activity = null;
        this.reviseRecord = null;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionStatus() {
        return descriptionStatus;
    }

    public void setDescriptionStatus(String descriptionStatus) {
        this.descriptionStatus = descriptionStatus;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Date getDateAct() {
        return dateAct;
    }

    public LocalDate getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(LocalDate dateStatus) {
        this.dateStatus = dateStatus;
    }

    public void setDateAct(Date date) {
        this.dateAct = date;
    }

    public SafetyActivities4Covid19.Mode getMode() {
        return mode;
    }

    public void setMode(SafetyActivities4Covid19.Mode mode) {
        this.mode = mode;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public SafetyActivities4Covid19.Status getStatus() {
        return status;
    }

    public void setStatus(SafetyActivities4Covid19.Status status) {
        this.status = status;
    }

    public Date getReviseRecord() {
        return reviseRecord;
    }

    public void setReviseRecord(Date date) {
        this.reviseRecord = date;
    }

    public void update(SafetyActivities4Covid19.Status status, Date date, String description) {
        this.setStatus(status);
        this.setReviseRecord(date);
        this.setDescriptionStatus(description);
    }

    public boolean isEnabled() {
        return this.status == SafetyActivities4Covid19.Status.ENABLED;
    }

    public Activity newActivity() {
        Activity newActivity = new Activity(this.actId, this.description, this.dateAct, this.mode, this.num, this);
        this.organization.addActivity(newActivity);
        this.activity = newActivity;
        return activity;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public int compareTo(Record o) {
        int ret = 0;
        LocalDate date1 = this.getDateStatus();
        LocalDate date2 = o.getDateStatus();

        if(date1.compareTo(date2) == 0){
            Integer sizeOrg1 = this.getOrganization().numWorkers();
            Integer sizeOrg2 = o.getOrganization().numWorkers();
            ret = sizeOrg1.compareTo(sizeOrg2);
        } else {
            ret = date1.compareTo(date2);
        }
        return ret;
    }
}
