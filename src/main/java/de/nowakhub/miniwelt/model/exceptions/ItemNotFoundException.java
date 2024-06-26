package de.nowakhub.miniwelt.model.exceptions;

public class ItemNotFoundException extends PublicException {

    public ItemNotFoundException() {
        super("Can not pick up item, actor did not find an item.");
    }

    public ItemNotFoundException(String message) {
        super(message);
    }
}