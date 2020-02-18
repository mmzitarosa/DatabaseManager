package it.mmzitarosa.databasemanager.manager;

import it.mmzitarosa.databasemanager.model.Category;

public class CategoryManager extends DatabaseManager {

    private static CategoryManager instance = null;

    CategoryManager() throws Exception {
        super();
    }

    public static CategoryManager getInstance() throws Exception {
        if (instance == null) {
            instance = new CategoryManager();
        }
        return instance;
    }

    Class<?> setTable() {
        return Category.class;
    }

}
