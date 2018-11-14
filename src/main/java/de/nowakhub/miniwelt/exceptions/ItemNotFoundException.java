package de.nowakhub.miniwelt.exceptions;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException() {
        super("Can not pick up item, actor did not find an item.");
    }

    public ItemNotFoundException(String message) {
        super(message);
    }
}