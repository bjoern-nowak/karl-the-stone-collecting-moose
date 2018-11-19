package de.nowakhub.miniwelt.model.exceptions;

public class NoClearPathException extends RuntimeException {

    public NoClearPathException() {
        super("Can not move forward, obstacle is ahead of actor.");
    }

    public NoClearPathException(String message) {
        super(message);
    }
}