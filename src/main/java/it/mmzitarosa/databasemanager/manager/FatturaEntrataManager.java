package it.mmzitarosa.databasemanager.manager;

import it.mmzitarosa.databasemanager.model.FatturaEntrata;

public class FatturaEntrataManager extends DatabaseManager {

    private static FatturaEntrataManager instance = null;

    FatturaEntrataManager() throws Exception {
        super();
    }

    public static FatturaEntrataManager getInstance() throws Exception {
        if (instance == null) {
            instance = new FatturaEntrataManager();
        }
        return instance;
    }

    Class<?> setTable() {
        return FatturaEntrata.class;
    }


}
