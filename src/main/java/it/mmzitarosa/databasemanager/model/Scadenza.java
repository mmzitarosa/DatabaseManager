package it.mmzitarosa.databasemanager.model;

import it.mmzitarosa.databasemanager.annotation.*;

public class Scadenza {

    @Id @Auto
    private int id;
    @Required
    @ForeignKey
    private FatturaEntrata fatturaEntrata;
    @Required
    @Size(13)
    private long ts;
    @Size(13)
    private long newTs;
    @Required
    private double importo;
    @Required
    private boolean chiusa = false;
}
