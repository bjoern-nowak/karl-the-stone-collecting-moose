package de.nowakhub.miniwelt.model.exceptions;

public class InvalidWorldSizeException extends RuntimeException {

    public InvalidWorldSizeException() {
        super("World cannot be smaller then 2x2.");
    }

    public InvalidWorldSizeException(String message) {
        super(message);
    }
}