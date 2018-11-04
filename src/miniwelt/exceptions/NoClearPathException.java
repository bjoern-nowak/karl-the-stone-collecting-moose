package miniwelt.exceptions;

public class NoClearPathException extends RuntimeException {

    public NoClearPathException() {
    }

    public NoClearPathException(String message) {
        super(message);
    }
}