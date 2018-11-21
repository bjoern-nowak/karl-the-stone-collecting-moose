package de.nowakhub.miniwelt.model.exceptions;

public class PublicException extends RuntimeException {

    public PublicException() {
    }

    public PublicException(String message) {
        super(message);
    }

    public PublicException(String message, Throwable cause) {
        super(message, cause);
    }

    public PublicException(Throwable cause) {
        super(cause);
    }

    public PublicException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
