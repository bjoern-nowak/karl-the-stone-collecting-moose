package miniwelt.exceptions;

public class NotInOfficeException extends RuntimeException {

    public NotInOfficeException() {
    }

    public NotInOfficeException(String message) {
        super(message);
    }
}