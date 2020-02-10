package it.mmzitarosa.databasemanager.model;

import it.mmzitarosa.databasemanager.annotation.*;

public class Categoria {

    @Id
    @Auto
    private int id;
    @Required
    @Size(40)
    private String nome;
    @ForeignKey
    private Categoria sottocategoria;

    public Categoria() {

    }

    public Categoria(String nome) {
        this.nome = nome;
    }

    public Categoria(String nome, Categoria sottocategoria) {
        this.nome = nome;
        this.sottocategoria = sottocategoria;
    }

    public void setId(int id) {
        this.id = id;
    }
}
