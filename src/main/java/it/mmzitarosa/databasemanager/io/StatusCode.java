package it.mmzitarosa.databasemanager.io;

public enum StatusCode {

    SUCCESS(200),
    INPUT_MISSING_PARKEY(-1000, "Missing input key or value."),
    USER_NOT_LOGGED(-2000, "User is not logged."),
    USER_ALREADY_LOGGED(-2100, "Already logged in."),
    USER_ALREADY_EXIST(-2200, "Username already exist."),
    USER_CREDENTIAL_NOT_VALID(-2300, "User credentials are not valid."),
    USER_NOT_ADMIN(-2400, "User is not an administrator."),
    USER_SL_TOKEN_NOT_VALID(-2500, "Silent Login token is not valid."),
    USER_SL_TOKEN_EXPIRED(-2600, "Silent Login token is expired."),
    USER_IN_USE_NOT_FOUND(-2700, "Not found current user."),
    DATABASE_ERROR(-3000, "Database error."),
    DATABASE_EMPTY_RESPONSE(-3100, "No database rows found for the request."),
    DATABASE_JOIN_ERROR(-3700, "Database Join generic errror."),
    DATABASE_JOIN_MANAGER_ERROR(-3800, "Database manager not set in join."),
    DATABASE_JOIN_EMPTY_KEY(-3900, "Null or empty key."),
    SECURE_GENERIC_ERROR(-4000, "Secure generic error."),
    SECURE_PASSWORD_TOO_SHORT(-4100, "Password is too short, min length is 32 chars."),
    SQL_ERROR(-5000, "Sql error."),
    GENERIC_ERROR(-6000),
    ;

    private int statusCode;
    private String defMessage;

    StatusCode(int statusCode) {
        this.statusCode = statusCode;
        this.defMessage = "";
    }

    StatusCode(int statusCode, String defMessage) {
        this.statusCode = statusCode;
        this.defMessage = defMessage;
    }

    public int value() {
        return statusCode;
    }

    public String message() {
        return defMessage;
    }

    public static StatusCode fromInt(int statusCode) {
        for (StatusCode status : StatusCode.values()) {
            if (status.value() == statusCode) {
                return status;
            }
        }
        StatusCode customStatusCode = GENERIC_ERROR;
        customStatusCode.statusCode = statusCode;
        return customStatusCode;
    }
}