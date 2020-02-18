package it.mmzitarosa.databasemanager.manager.sql;

import it.mmzitarosa.databasemanager.io.StatusCode;
import it.mmzitarosa.databasemanager.util.BaseException;

public class SqlException extends BaseException {

    public SqlException(String message) {
        super(message);
    }

    public SqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlException(Throwable cause) {
        super(cause);
    }

    public SqlException(StatusCode statusCode) {
        super(statusCode);
    }

    public SqlException(StatusCode statusCode, String message) {
        super(statusCode, message);
    }

    public SqlException(StatusCode statusCode, String message, Throwable cause) {
        super(statusCode, message, cause);
    }

    public SqlException(StatusCode statusCode, Throwable cause) {
        super(statusCode, cause);
    }

    @Override
    public StatusCode setDefaultStatusCode() {
        return StatusCode.SQL_ERROR;
    }

}
