package it.mmzitarosa.databasemanager.io;

import it.mmzitarosa.databasemanager.util.GsonManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class OkOutput extends Output {

    public OkOutput() {
        super(Status.OK, StatusCode.SUCCESS);
    }

    public OkOutput(String message) {
        super(Status.OK, StatusCode.SUCCESS, message);
    }

    public OkOutput(JSONObject json) {
        super(Status.OK, StatusCode.SUCCESS, json);
    }

    public <T> OkOutput(T object) {
        this(new JSONObject(GsonManager.getInstance().toJson(object)));
    }

    public OkOutput(JSONArray json) {
        super(Status.OK, StatusCode.SUCCESS, json);
    }

    public <T> OkOutput(List<T> objectList) {
        this(new JSONArray(GsonManager.getInstance().toJson(objectList)));
    }

}
