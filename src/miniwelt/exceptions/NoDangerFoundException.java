package miniwelt.exceptions;

public class NoDangerFoundException extends RuntimeException {

    public NoDangerFoundException() {
    }

    public NoDangerFoundException(String message) {
        super(message);
    }
}