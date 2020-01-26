package it.mmzitarosa.databasemanager;

import it.mmzitarosa.databasemanager.manager.CategoriaManager;
import it.mmzitarosa.databasemanager.model.Categoria;

import java.util.List;

public class MainClass {

    public static void main(String[] args) throws Exception {
        List<Categoria> list = CategoriaManager.getInstance().selectAll();
        System.out.println("");
    }

}
