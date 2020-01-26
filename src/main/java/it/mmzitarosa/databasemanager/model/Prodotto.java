package it.mmzitarosa.databasemanager.model;

import it.mmzitarosa.databasemanager.annotation.*;

public class Prodotto {

    @Id @Auto
    private int id;
    @Size(40)
    private String codice;
    @Required
    @ForeignKey
    private Categoria categoria;
    @Required
    @Size(50)
    private String marca;
    @Size(100)
    private String descrizione;
    private double prezzoVendita;
}
