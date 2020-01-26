package it.mmzitarosa.databasemanager.io;


import it.mmzitarosa.databasemanager.util.GuitarBaseException;

public class KoOutput extends Output {

    public KoOutput(GuitarBaseException e) {
        super(Status.KO, e.getStatusCode(), e.hasMessage() ? e.getMessage() : e.getStatusCode().message());
    }

}
