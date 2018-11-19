package de.nowakhub.miniwelt.model.exceptions;

public class BagFullException extends RuntimeException {

    public BagFullException() {
        super("Can not pick up item, actors bag is full.");
    }

    public BagFullException(String message) {
        super(message);
    }
}