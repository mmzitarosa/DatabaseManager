package it.mmzitarosa.databasemanager.model;

import it.mmzitarosa.databasemanager.annotation.Auto;
import it.mmzitarosa.databasemanager.annotation.Id;
import it.mmzitarosa.databasemanager.annotation.Required;
import it.mmzitarosa.databasemanager.annotation.Size;

public class Fornitore {

    @Id @Auto
    private int id;
    @Required
    @Size(100) //@Unique
    private String nome;

}
