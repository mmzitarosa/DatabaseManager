package it.mmzitarosa.databasemanager.manager;

import it.mmzitarosa.databasemanager.io.StatusCode;
import it.mmzitarosa.databasemanager.util.BaseException;

public class DatabaseException extends BaseException {

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }

    public DatabaseException(StatusCode statusCode) {
        super(statusCode);
    }

    public DatabaseException(StatusCode statusCode, String message) {
        super(statusCode, message);
    }

    public DatabaseException(StatusCode statusCode, String message, Throwable cause) {
        super(statusCode, message, cause);
    }

    public DatabaseException(StatusCode statusCode, Throwable cause) {
        super(statusCode, cause);
    }

    @Override
    public StatusCode setDefaultStatusCode() {
        return StatusCode.DATABASE_ERROR;
    }
}
