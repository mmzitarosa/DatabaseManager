package it.mmzitarosa.databasemanager;

import it.mmzitarosa.databasemanager.manager.CategoriaManager;
import it.mmzitarosa.databasemanager.model.Categoria;
import it.mmzitarosa.databasemanager.util.GsonManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainClass {

    public static void main(String[] args) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 8);
        List<Categoria> list = CategoriaManager.getInstance().select(map);
        Categoria categoria = new Categoria("testSottocategoria", list.get(0));
        int i = CategoriaManager.getInstance().insert(categoria);
        map.clear();
        map.put("id", i);
        list = CategoriaManager.getInstance().select(map);
        System.out.println(GsonManager.getInstance().toJson(list));
    }

}
