package de.nowakhub.miniwelt.exceptions;

public class RequireActorException extends RuntimeException {

    public RequireActorException() {
        super("Can not run game, actor is missing in world.");
    }

    public RequireActorException(String message) {
        super(message);
    }
}