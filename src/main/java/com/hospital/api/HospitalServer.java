    package com.hospital.api;

import com.hospital.api.handlers.RouterHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HospitalServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/patients", new RouterHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("--Hospital API with SEPARATE HANDLERS on http://localhost:8080");
        System.out.println("--RouterHandler → PatientHandler");
        System.out.println("--RouterHandler → AppointmentHandler");
        System.out.println("--RouterHandler → MedicalRecordHandler");
    }
}
