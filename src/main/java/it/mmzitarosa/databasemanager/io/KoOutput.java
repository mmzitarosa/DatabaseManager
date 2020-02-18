package it.mmzitarosa.databasemanager.io;


import it.mmzitarosa.databasemanager.util.BaseException;

public class KoOutput extends Output {

    public KoOutput(BaseException e) {
        super(Status.KO, e.getStatusCode(), e.hasMessage() ? e.getMessage() : e.getStatusCode().message());
    }

}
