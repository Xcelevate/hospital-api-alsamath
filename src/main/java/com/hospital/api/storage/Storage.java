package com.hospital.api.storage;

import com.hospital.api.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Storage {

    private static final Map<String,Patient> PATIENTS = new ConcurrentHashMap<>();
    private static final Map<String, List<Appointment>> APPOINTMENTS = new ConcurrentHashMap<>();
    private static final Map<String,List<MedicalRecord>> RECORDS = new ConcurrentHashMap<>();


    static {
        Patient p1 = new Patient("patient-1","Alsamath","1980-01-15");
        p1.setGender("M");
        p1.setPhone("+919876543210");
        PATIENTS.put("patient-1",p1);

        List<Appointment> appts = new ArrayList<>();
        appts.add(new Appointment("appt-1","patient-1","doctor-101","2026-02-20","10:00"));
        APPOINTMENTS.put("patient-1",appts);

        List<MedicalRecord> records = new ArrayList<>();
        records.add(new MedicalRecord("record-1", "patient-1", "Flu","2026-02-15"));
        RECORDS.put("patient-1",records);
    }

    public static Map<String,Patient> getPatients() {
        return PATIENTS;
    }

    public static Map<String,List<Appointment>> getAppointments(){
        return APPOINTMENTS;
    }
    public static Map<String,List<MedicalRecord>> getRecords(){
        return RECORDS;
    }
}
