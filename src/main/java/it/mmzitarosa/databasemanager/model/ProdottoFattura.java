package it.mmzitarosa.databasemanager.model;

import it.mmzitarosa.databasemanager.annotation.ForeignKey;
import it.mmzitarosa.databasemanager.annotation.Required;

public class ProdottoFattura {

    @Required
    @ForeignKey
    private FatturaEntrata fattura;
    @Required
    @ForeignKey
    private Prodotto prodotto;
    @Required
    private int quantita;
    @Required
    private double prezzoCosto;

}
