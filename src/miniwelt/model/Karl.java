package miniwelt.model;

import miniwelt.exceptions.*;

public class Karl {

    public static final int BAG_MAX = 3;

    // has
    private int bag = 0;
    public Direction dir = Direction.UP;
    public Position office;
    public Position pos;

    // knows about
    public Field[][] grid;

    // Bewegungs-Befehle

    public void stepForward() throws NoClearPathException {
        if (!aheadClear()) throw new NoClearPathException();
        pos.x += dir.x;
        pos.y += dir.y;
        if (grid[pos.x][pos.y].equals(Field.DANGER)) grid[pos.x][pos.y] = Field.KARL_ON_DANGER;
        if (grid[pos.x][pos.y].equals(Field.OFFICE)) grid[pos.x][pos.y] = Field.KARL_AT_OFFICE;
    }

    public void turnRight() {
        switch (dir) {
            case UP: dir = Direction.RIGHT;
            case DOWN: dir = Direction.LEFT;
            case LEFT: dir = Direction.UP;
            case RIGHT: dir = Direction.DOWN;
        }
    }

    // Zusatzbefehl zur Schwierigkeitsreduktion
    public void backToOffice() {

    }


    // Aktions-Befehle

    public void takeUp() throws BagFullException, NoDangerFoundException {
        if (!foundDanger()) throw new NoDangerFoundException();
        if (bag == BAG_MAX) throw new BagFullException();
        bag++;
        grid[pos.x][pos.y] = Field.KARL;
    }

    public void dropDown() throws BagEmptyException, NotInOfficeException {
        if (bagEmpty()) throw new BagEmptyException();
        if (!grid[pos.x][pos.y].equals(Field.KARL_AT_OFFICE)) throw new NotInOfficeException();
        bag--;

    }


    // Test-Befehle

    public boolean aheadClear() {
        return !grid[pos.x + dir.x][pos.y + dir.y].equals(Field.WALL);
    }

    public boolean bagEmpty() {
        return bag == 0;
    }

    public boolean foundDanger() {
        return grid[pos.x][pos.y].equals(Field.KARL_ON_DANGER);
    }

}