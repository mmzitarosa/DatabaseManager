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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getSottocategoria() {
        return sottocategoria;
    }

    public void setSottocategoria(Categoria sottocategoria) {
        this.sottocategoria = sottocategoria;
    }
}
