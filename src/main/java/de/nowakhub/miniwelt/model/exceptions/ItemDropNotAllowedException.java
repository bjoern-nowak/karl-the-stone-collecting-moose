package de.nowakhub.miniwelt.model.exceptions;

public class ItemDropNotAllowedException extends PublicException {

    public ItemDropNotAllowedException() {
        super("Can not drop item here, actor has to be at start point.");
    }

    public ItemDropNotAllowedException(String message) {
        super(message);
    }
}