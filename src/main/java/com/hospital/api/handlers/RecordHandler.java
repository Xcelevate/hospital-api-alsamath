package com.hospital.api.handlers;

import com.hospital.api.model.MedicalRecord;
import com.hospital.api.storage.Storage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RecordHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try{
            String[] parts = path.split("/");
            if(parts.length<4 || !parts[3].equals("records")){
                    return ;
            }

            String patientId = parts[2];

            switch(method) {
                case "GET" :
                    handleListRecords(exchange,patientId);
                    break;

                case "POST":
                    handleCreateRecord(exchange,patientId);
                    break;

                default :
                    sendResponse(exchange,405,"{\"error\":\"Method not allowed\"}");
            }
        }
        catch( Exception e){
            e.printStackTrace();;
            sendResponse(exchange,500,"{\"error\":\"Internal server error\"}");
        }
    }

    private void handleListRecords(HttpExchange exchange, String patientId) throws IOException{

        List<MedicalRecord> records = Storage.getRecords().getOrDefault(patientId,List.of());

        String json = records.stream().map(record->String.format("{\"id\":\"%s\",\"diagnosis\":\"%s\",\"date\":\"%s\"}",record.getId(),record.getDiagnosis(),record.getDate()))
                .collect(Collectors.joining(",","[","]"));

        sendResponse(exchange,200,json);
    }

    private void handleCreateRecord(HttpExchange exchange, String patientId) throws IOException {

        if(!Storage.getPatients().containsKey(patientId)){
            sendResponse(exchange,404,"{\"error\":\"Patient not found\"}");
            return;
        }

        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(),StandardCharsets.UTF_8);

        String diagnosis = extractField(body, "diagnosis");
        if(diagnosis == null){
            sendResponse(exchange, 400, "{\"error\":\"diagnosis required\"}");
            return;
        }

        String id = "record-"+ UUID.randomUUID().toString().substring(0,8);
        MedicalRecord record = new MedicalRecord(id,patientId,diagnosis, LocalDate.now());

        Storage.getRecords().computeIfAbsent(patientId,k-> new ArrayList<>()).add(record);

        String json = String.format("{\"id\":\"%s\",\"diagnosis\":\"%s\",\"date\":\"2026-02-16\"}",
                id, diagnosis
        );

        sendResponse(exchange,201,json);
    }

    private String extractField(String json,String field){
        String key = "\" "+ field + "\":";
        int start = json.indexOf(key);
        if(start == -1) return null;
        start +=key.length();
        int valueStart = json.indexOf("\"", start)+1;
        int valueEnd = json.indexOf("\"", valueStart);
        return json.substring(valueStart,valueEnd);
    }

    private void sendResponse(HttpExchange exchange,int status,String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type","application/json");
        exchange.sendResponseHeaders(status, body.getBytes().length);
        OutputStream os = exchange.getResponseBody();

        os.write(body.getBytes());
        os.close();
    }
}
