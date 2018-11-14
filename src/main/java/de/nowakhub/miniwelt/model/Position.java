package de.nowakhub.miniwelt.model;

public class Position {

    public int x = -1;
    public int y = -1;

    public Position() {
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean exists() {
        return x > -1 && y > -1;
    }
}
