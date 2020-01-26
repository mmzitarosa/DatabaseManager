package it.mmzitarosa.databasemanager.model;

import it.mmzitarosa.databasemanager.annotation.*;

public class Categoria {

    @Id @Auto
    private int id;
    @Required
    @Size(40)
    private String nome;
    @ForeignKey
    private Categoria sottocategoria;

}
