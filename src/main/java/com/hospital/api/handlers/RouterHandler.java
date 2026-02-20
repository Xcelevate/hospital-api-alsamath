package com.hospital.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.IOException;
import java.net.URI;

public class RouterHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        // ROUTE TO CORRECT HANDLER BASED ON PATH
        HttpHandler targetHandler = null;

        if (parts.length >= 2 && "patients".equals(parts[1])) {
            if (parts.length == 4) {
                if ("appointments".equals(parts[3])) {
                    targetHandler = new AppointmentHandler();
                } else if ("records".equals(parts[3])) {
                    targetHandler = new RecordHandler();
                }
            }
            if (targetHandler == null) {
                targetHandler = new PatientHandler();
            }
        } else {
            sendError(exchange, 404, "Not found");
            return;
        }

        // DELEGATE TO TARGET HANDLER
        targetHandler.handle(exchange);
    }

    private void sendError(HttpExchange exchange, int status, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, message.getBytes().length);
        exchange.getResponseBody().write(message.getBytes());
        exchange.close();
    }
}
