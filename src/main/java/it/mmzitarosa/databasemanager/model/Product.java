package it.mmzitarosa.databasemanager.model;

import it.mmzitarosa.databasemanager.annotation.*;

public class Product {

    @Id
    @Auto
    private int id;
    @Size(40)
    private String code;
    @Required
    @ForeignKey
    private Category category;
    @Required
    @Size(50)
    private String brand;
    @Size(100)
    private String description;
    private double price;
}
