package it.mmzitarosa.databasemanager.util;

import it.mmzitarosa.databasemanager.io.StatusCode;

public abstract class BaseException extends Exception {

    private StatusCode statusCode = setDefaultStatusCode();

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(StatusCode statusCode) {
        super();
        this.statusCode = statusCode;
    }

    public BaseException(StatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public BaseException(StatusCode statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public BaseException(StatusCode statusCode, Throwable cause) {
        super(cause);
        this.statusCode = statusCode;
    }

    public boolean hasMessage() {
        return getMessage() != null;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public abstract StatusCode setDefaultStatusCode();

    @Override
    public String toString() {
        String s = getClass().getName();
        String message = getLocalizedMessage();
        return s + ": " + statusCode.name() + " (" + statusCode.value() + ")" + ": " + ((message != null) ? message : statusCode.message());
    }
}
