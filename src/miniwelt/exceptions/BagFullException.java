package miniwelt.exceptions;

public class BagFullException extends RuntimeException {

    public BagFullException() {
    }

    public BagFullException(String message) {
        super(message);
    }
}