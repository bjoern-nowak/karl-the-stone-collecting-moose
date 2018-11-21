package de.nowakhub.miniwelt.model.exceptions;

public class BagEmptyException extends PublicException {

    public BagEmptyException() {
        super("Can not drop item, actors bag is empty.");
    }

    public BagEmptyException(String message) {
        super(message);
    }
}