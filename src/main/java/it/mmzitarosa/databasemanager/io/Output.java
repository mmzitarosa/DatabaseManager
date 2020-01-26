package it.mmzitarosa.databasemanager.io;

import org.json.JSONObject;

public class Output {

    private Status status;
    private StatusCode statusCode;
    private Object message;

    Output(Status status, StatusCode statusCode) {
        this(status, statusCode, "");
    }

    Output(Status status, StatusCode statusCode, Object message) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("status", status.name());
        json.put("statusCode", statusCode.value());
        json.put("message", message);
        return json.toString();
    }

    public String json() {
        return this.toString();
    }

    enum Status {
        OK,
        KO;
    }

}
