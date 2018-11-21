package de.nowakhub.miniwelt.model.exceptions;

public class RequireActorException extends PublicException {

    public RequireActorException() {
        super("Can not run game, actor is missing in world.");
    }

    public RequireActorException(String message) {
        super(message);
    }
}