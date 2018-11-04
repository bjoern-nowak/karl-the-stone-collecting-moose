package miniwelt.exceptions;

public class GridPositionOccupiedException extends RuntimeException {

    public GridPositionOccupiedException() {
    }

    public GridPositionOccupiedException(String message) {
        super(message);
    }
}