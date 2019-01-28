package de.nowakhub.miniwelt.model.util;

import java.io.Serializable;

public enum Direction implements Serializable {

    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    static final long serialVersionUID = 1L;
    public int row, col;

    Direction(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Direction turnRight() {
        switch (this) {
            case UP: return Direction.RIGHT;
            case DOWN: return Direction.LEFT;
            case LEFT: return Direction.UP;
            case RIGHT: return Direction.DOWN;
        }
        return null;
    }
}
