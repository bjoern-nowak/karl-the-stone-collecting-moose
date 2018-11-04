package miniwelt.exceptions;

public class BagEmptyException extends RuntimeException {

    public BagEmptyException() {
    }

    public BagEmptyException(String message) {
        super(message);
    }
}