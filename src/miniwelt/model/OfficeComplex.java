package miniwelt.model;

public class OfficeComplex {


    private Field[][] grid; // or use linked list (drawback: consumes more memory, may be a performance issue)
    private int max_X;
    private int max_Y;

    private Karl karl;

    public OfficeComplex(int max_X, int max_Y) {
        karl = new Karl();
        resize(max_X, max_Y);
    }

    public void resize(int max_X, int max_Y) {
        this.max_X = max_X;
        this.max_Y = max_Y;

        Field[][] oldGrid = grid;
        grid = new Field[max_X][max_Y];

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                if (oldGrid == null) {
                    grid[x][y] = Field.EMPTY;
                } else if (x < oldGrid.length && y < oldGrid[x].length) {
                    grid[x][y] = oldGrid[x][y];
                    if (isOccupiedBy(x, y, Field.KARL)) setPos(karl.pos, x, y);
                    if (isOccupiedBy(x, y, Field.OFFICE)) setPos(karl.office, x, y);
                }
            }

        }
        
        karl.grid = grid;
    }

    public void placeWall(int x, int y) throws ArrayIndexOutOfBoundsException {
        checkBoundaries(x, y);
        grid[x][y] = Field.WALL;
        checkOverrideOfKarlOrOffice(x, y);
    }

    public void placeDanger(int x, int y) throws ArrayIndexOutOfBoundsException {
        checkBoundaries(x, y);
        if (isOccupiedBy(x, y, Field.KARL)) {
            grid[x][y] = Field.KARL_ON_DANGER;
        } else {
            grid[x][y] = Field.DANGER;
        }
    }

    public void placeKarl(int x, int y) throws ArrayIndexOutOfBoundsException {
        checkBoundaries(x, y);
        setPos(karl.pos, x, y);
        if (isOccupiedBy(x, y, Field.OFFICE)) {
            grid[x][y] = Field.KARL_AT_OFFICE;
        } else if (isOccupiedBy(x, y, Field.DANGER)) {
            grid[x][y] = Field.KARL_ON_DANGER;
        } else {
            grid[x][y] = Field.KARL;
        }
    }

    public void placeOffice(int x, int y) throws ArrayIndexOutOfBoundsException {
        checkBoundaries(x, y);
        
        // remove old office from grid
        if (isOccupiedByKarl(karl.office.x, karl.office.y)) {
            grid[karl.office.x][karl.office.y] = Field.KARL;
        } else {
            grid[karl.office.x][karl.office.y] = Field.EMPTY;
        }
        
        // place new office
        setPos(karl.office, x, y);
        if (isOccupiedByKarl(x, y)) {
            grid[x][y] = Field.KARL_AT_OFFICE;
        } else {
            grid[x][y] = Field.OFFICE;
        }
    }

    public void remove(int x, int y) {
        grid[x][y] = Field.EMPTY;
    }

    // check placement: if x and y position exceeds grid size
    public void checkBoundaries(int x, int y) throws ArrayIndexOutOfBoundsException {
        if (x >= max_X || y >= max_Y) throw new ArrayIndexOutOfBoundsException("Position X or Y exceeds grid size.");
    }

    
    public void checkOverrideOfKarlOrOffice(int x, int y) {
        
    }

    public boolean isOccupiedBy(int x, int y, Field field) {
        return grid[x][y].equals(field);
    }

    public boolean isOccupiedByKarl(int x, int y) {
        return grid[x][y].equals(Field.KARL) 
                || grid[x][y].equals(Field.KARL_AT_OFFICE) 
                || grid[x][y].equals(Field.KARL_ON_DANGER);
    }

    private void setPos(Position pos, int x, int y) {
        pos.x = x;
        pos.y = y;
    }

}