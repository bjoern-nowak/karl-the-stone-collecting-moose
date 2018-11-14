package de.nowakhub.miniwelt.exceptions;

public class RequireStartException extends RuntimeException {

    public RequireStartException() {
        super("Can not run game, start point is missing in world.");
    }

    public RequireStartException(String message) {
        super(message);
    }
}