package com.hospital.api.model;

import java.time.LocalDate;

public class MedicalRecord {

    private String id;
    private String patientId;
    private String diagnosis;
    private String treatment;
    private String notes;
    private LocalDate date;

    public MedicalRecord(){}

    public MedicalRecord(String id, String patientId, String diagnosis, LocalDate date){
        this.id = id;
        this.patientId = patientId;
        this.diagnosis = diagnosis;
        this.date = date;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    @Override
    public String toString(){
        return "Medical Record {id='"+ id + "',diagnosis='"+ diagnosis +"'}";
    }
}
