package de.nowakhub.miniwelt.model.util;

import java.io.Serializable;

public class Position implements Serializable {
    static final long serialVersionUID = 1L;

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


    // _________________________________________________________________________________________________________________
    //     getter/setter for export (XML)
    // -----------------------------------------------------------------------------------------------------------------

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
