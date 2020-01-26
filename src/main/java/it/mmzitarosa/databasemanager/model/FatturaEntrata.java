package it.mmzitarosa.databasemanager.model;

import it.mmzitarosa.databasemanager.annotation.*;

public class FatturaEntrata {

    @Id @Auto
    private int id;
    @Required
    @ForeignKey
    private Fornitore fornitore;
    @Required
    @Size(13)
    private long ts;
    @Required
    private double importo;
    @Required
    @Size(50)
    private String numero;
    @Required
    private boolean chiusa = false;

}
