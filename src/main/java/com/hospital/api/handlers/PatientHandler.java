package com.hospital.api.handlers;

import com.hospital.api.model.Patient;
import com.hospital.api.storage.Storage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PatientHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        // ONLY HANDLE PATIENT ROOT AND SINGLE PATIENT PATHS
        if (parts.length > 3) {
            sendResponse(exchange, 404, "{\"error\": \"Use RouterHandler for sub-resources\"}");
            return;
        }

        try {
            switch (method) {
                case "GET":
                    if (parts.length == 2) { // /patients
                        handleListPatients(exchange);
                    } else if (parts.length == 3) { // /patients/{id}
                        handleGetPatient(exchange, parts[2]);
                    }
                    break;
                case "POST":
                    if (parts.length == 2) { // /patients
                        handleCreatePatient(exchange);
                    }
                    break;
                default:
                    sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Server error\"}");
        }
    }


    private void handleListPatients(HttpExchange exchange) throws IOException {
        // WHY: Convert patients to JSON array
        Map<String, Patient> patients = Storage.getPatients();
        String json = patients.values().stream()
                .map(p -> "{\"id\":\"" + p.getId() + "\",\"name\":\"" + p.getName() + "\"}")
                .collect(Collectors.joining(",", "[", "]"));

        sendResponse(exchange, 200, json);
    }

    private void handleGetPatient(HttpExchange exchange, String id) throws IOException {
        Patient patient = Storage.getPatients().get(id);
        if (patient == null) {
            sendResponse(exchange, 404, "{\"error\": \"Patient not found\"}");
            return;
        }

        // WHY: Manual JSON building (no Jackson needed)
        String json = String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"dob\":\"%s\",\"gender\":\"%s\",\"phone\":\"%s\"}",
                patient.getId(), patient.getName(), patient.getDob(),
                patient.getGender(), patient.getPhone()
        );
        sendResponse(exchange, 200, json);
    }

    private void handleCreatePatient(HttpExchange exchange) throws IOException {
        // WHY: Read JSON body (simple string parsing)
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        // Simple parsing: extract name (improve later)
        String name = extractField(body, "name");
        String dob = extractField(body, "dob");

        if (name == null || name.trim().isEmpty()) {
            sendResponse(exchange, 400, "{\"error\": \"Name required\"}");
            return;
        }

        // Create patient
        String id = "patient-" + UUID.randomUUID().toString().substring(0, 8);
        Patient patient = new Patient(id, name, dob);
        Storage.getPatients().put(id, patient);

        String json = String.format("{\"id\":\"%s\",\"name\":\"%s\"}", id, name);
        exchange.getResponseHeaders().set("Location", "/patients/" + id);
        sendResponse(exchange, 201, json);
    }

    private String extractField(String json, String field) {
        // WHY: Simple manual JSON parsing (like conference assignment)
        String key = "\"" + field + "\":";
        int start = json.indexOf(key);
        if (start == -1) return null;
        start += key.length();

        char quote = '"';
        int valueStart = json.indexOf(quote, start) + 1;
        int valueEnd = json.indexOf(quote, valueStart);
        return json.substring(valueStart, valueEnd);
    }

    private void sendResponse(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, body.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(body.getBytes());
        os.close();
    }
}
