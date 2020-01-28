package it.mmzitarosa.databasemanager;

import it.mmzitarosa.databasemanager.manager.FatturaEntrataManager;
import it.mmzitarosa.databasemanager.model.FatturaEntrata;
import it.mmzitarosa.databasemanager.util.GsonManager;

import java.util.List;

public class MainClass {

    public static void main(String[] args) throws Exception {
        List<FatturaEntrata> list = FatturaEntrataManager.getInstance().selectAll();
        System.out.println(GsonManager.getInstance().toJson(list));
    }

}
