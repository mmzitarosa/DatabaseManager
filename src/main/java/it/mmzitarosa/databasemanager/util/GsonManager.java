package it.mmzitarosa.databasemanager.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonManager {

    private static Gson gson = null;

    private GsonManager() {
    }

    public static Gson getInstance() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();
            gson = gsonBuilder.create();
        }
        return gson;
    }

}
