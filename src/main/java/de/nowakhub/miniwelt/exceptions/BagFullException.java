package de.nowakhub.miniwelt.exceptions;

public class BagFullException extends RuntimeException {

    public BagFullException() {
        super("Can not pick up item, actors bag is full.");
    }

    public BagFullException(String message) {
        super(message);
    }
}