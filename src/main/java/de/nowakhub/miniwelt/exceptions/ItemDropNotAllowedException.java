package de.nowakhub.miniwelt.exceptions;

public class ItemDropNotAllowedException extends RuntimeException {

    public ItemDropNotAllowedException() {
        super("Can not drop item here, actor has to be at start point.");
    }

    public ItemDropNotAllowedException(String message) {
        super(message);
    }
}