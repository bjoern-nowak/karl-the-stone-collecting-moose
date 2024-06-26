package de.nowakhub.miniwelt.model.exceptions;

public class PositionInvalidException extends PublicException {

    public PositionInvalidException() {
        super("Can not place there, position outside of world.");
    }

    public PositionInvalidException(String message) {
        super(message);
    }
}