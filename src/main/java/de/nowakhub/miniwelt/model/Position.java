package de.nowakhub.miniwelt.model;

public class Position {

    public int row = -1;
    public int col = -1;

    public Position() {
    }

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void set(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void reset() {
        this.row = -1;
        this.col = -1;
    }

    public boolean exists() {
        return row > -1 && col > -1;
    }
}
