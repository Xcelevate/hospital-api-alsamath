package com.hospital.api.handlers;

import com.hospital.api.model.Appointment;
import com.hospital.api.storage.Storage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// WHY: Handles appointment-specific routes under /patients/{id}/appointments
public class AppointmentHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            String[] parts = path.split("/");
            if (parts.length < 4 || !parts[3].equals("appointments")) {
                return;
            }

            String patientId = parts[2];

            switch (method) {
                case "GET":
                    handleListAppointments(exchange, patientId);
                    break;
                case "POST":
                    handleCreateAppointment(exchange, patientId);
                    break;
                default:
                    sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Internal server error\"}");
        }
    }

    private void handleListAppointments(HttpExchange exchange, String patientId) throws IOException {
        List<Appointment> appointments = Storage.getAppointments().getOrDefault(patientId, List.of());

        String json = appointments.stream()
                .map(appt -> String.format(
                        "{\"id\":\"%s\",\"doctorId\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"status\":\"%s\"}",
                        appt.getId(), appt.getDoctorId(), appt.getDate(), appt.getTime(), appt.getStatus()
                ))
                .collect(Collectors.joining(",", "[", "]"));

        sendResponse(exchange, 200, json);
    }

    private void handleCreateAppointment(HttpExchange exchange, String patientId) throws IOException {
        if (!Storage.getPatients().containsKey(patientId)) {
            sendResponse(exchange, 404, "{\"error\": \"Patient not found\"}");
            return;
        }

        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        String doctorId = extractField(body, "doctorId");
        String date = extractField(body, "date");
        String time = extractField(body, "time");

        if (doctorId == null || date == null || time == null) {
            sendResponse(exchange, 400, "{\"error\": \"doctorId, date, time required\"}");
            return;
        }

        String apptId = "appt-" + UUID.randomUUID().toString().substring(0, 8);
        Appointment appt = new Appointment(apptId, patientId, doctorId, date, time);

        Storage.getAppointments().computeIfAbsent(patientId, k -> new ArrayList<>()).add(appt);

        String json = String.format(
                "{\"id\":\"%s\",\"doctorId\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"status\":\"SCHEDULED\"}",
                apptId, doctorId, date, time
        );

        sendResponse(exchange, 201, json);
    }

    private String extractField(String json, String field) {
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
