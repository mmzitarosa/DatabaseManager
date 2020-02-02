package it.mmzitarosa.databasemanager;

import it.mmzitarosa.databasemanager.manager.FatturaEntrataManager;
import it.mmzitarosa.databasemanager.model.FatturaEntrata;
import it.mmzitarosa.databasemanager.util.GsonManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainClass {

    public static void main(String[] args) throws Exception {
        Map<String, Object> condMap = new HashMap<>();
        condMap.put("fornitore.nome", "Gold Music");
        condMap.put("numero", "Gold Music");
//        List<FatturaEntrata> list = FatturaEntrataManager.getInstance().selectAll();
        List<FatturaEntrata> list = FatturaEntrataManager.getInstance().select(condMap);
        System.out.println(GsonManager.getInstance().toJson(list));
    }

}
