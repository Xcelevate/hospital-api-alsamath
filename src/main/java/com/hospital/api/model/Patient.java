package com.hospital.api.model;

public class Patient {

    private String id;
    private String name;
    private String dob;
    private String gender;
    private String phone;


    public Patient(){}

    public Patient(String id, String name, String dob ){
        this.id=id;
        this.name = name;
        this.dob = dob;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    @Override
    public String toString(){
        return "Patient{id='"+ id +"',name='"+ name +"'}";
    }
}
