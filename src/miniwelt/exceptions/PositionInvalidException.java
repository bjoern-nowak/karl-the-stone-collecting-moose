package miniwelt.exceptions;

public class PositionInvalidException extends RuntimeException {

    public PositionInvalidException() {
        super("Can not place there, position outside of world.");
    }

    public PositionInvalidException(String message) {
        super(message);
    }
}