package de.nowakhub.miniwelt.model.exceptions;

public class InternalUnkownFieldException extends RuntimeException {

    public InternalUnkownFieldException() {
        super();
    }

    public InternalUnkownFieldException(String message) {
        super(message);
    }
}