package it.mmzitarosa.databasemanager.model;

import it.mmzitarosa.databasemanager.annotation.*;

public class Pagamento {

    @Id @Auto
    private int id;
    @Required
    @Size(13)
    private long ts;
    @Required
    private double importo;
    @Required
    @ForeignKey
    private Scadenza scadenza;
    private String note;

}
