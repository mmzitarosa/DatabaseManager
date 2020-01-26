package it.mmzitarosa.databasemanager.manager;

import it.mmzitarosa.databasemanager.model.Categoria;

public class CategoriaManager extends DatabaseManager {

    private static CategoriaManager instance = null;

    CategoriaManager() throws Exception {
        super();
    }

    public static CategoriaManager getInstance() throws Exception {
        if(instance==null){
            instance = new CategoriaManager();
        }
        return instance;
    }

    Class<?> setTable() {
        return Categoria.class;
    }



}
