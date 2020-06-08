package com.workoss.boot.autoconfigure.sapjco.client;

public class JCoClientException extends RuntimeException{

    public JCoClientException(String message) {
        super(message);
    }

    public JCoClientException(Throwable cause) {
        super(cause);
    }

    public JCoClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
