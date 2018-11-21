package de.nowakhub.miniwelt.model.exceptions;

public class RequireStartException extends PublicException {

    public RequireStartException() {
        super("Can not run game, start point is missing in world.");
    }

    public RequireStartException(String message) {
        super(message);
    }
}