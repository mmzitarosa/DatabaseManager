package it.mmzitarosa.databasemanager.model;

import it.mmzitarosa.databasemanager.annotation.*;

public class Category {

    @Id
    @Auto
    private int id;
    @Required
    @Size(40)
    private String name;
    @ForeignKey
    private Category subCateogry;

    public Category() {

    }

    public Category(String nome) {
        this.name = nome;
    }

    public Category(String nome, Category subCateogry) {
        this.name = nome;
        this.subCateogry = subCateogry;
    }

    public void setId(int id) {
        this.id = id;
    }
}
