package de.nowakhub.miniwelt.exceptions;

public class InternalUnkownFieldException extends RuntimeException {

    public InternalUnkownFieldException() {
        super();
    }

    public InternalUnkownFieldException(String message) {
        super(message);
    }
}