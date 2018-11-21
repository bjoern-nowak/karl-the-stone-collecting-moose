package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.model.exceptions.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class World extends Observable<Controllable> implements Controllable, Interactable {

    // limited by
    private static final int BAG_MAX = 3;
    private static final int MIN_SIZE = 2;
    private IntegerProperty sizeRow = new SimpleIntegerProperty();
    private IntegerProperty sizeCol = new SimpleIntegerProperty();

    // has
    private Field[][] field; // or use linked list (drawback: consumes more memory, may be a performance issue)
    private Position startPos = new Position();
    private Position actorPos = new Position();
    private Direction actorDir = Direction.UP;
    private int actorBag = 0;

    public World(int sizeRow, int sizeY) throws InvalidWorldSizeException {
        resize(sizeRow, sizeY);
    }

    //__________________________________________________________________________________________________________________
    //    Controllable - commands
    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void resize(int sizeRow, int sizeCol) throws InvalidWorldSizeException {
        if (MIN_SIZE > sizeRow || MIN_SIZE > sizeCol) throw new InvalidWorldSizeException();

        this.sizeRow.set(sizeRow);
        this.sizeCol.set(sizeCol);

        Field[][] oldGrid = field;
        field = new Field[sizeRow][sizeCol];
        actorPos = new Position();
        startPos = new Position();

        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                if (oldGrid != null && row < oldGrid.length && col < oldGrid[row].length) {
                    field[row][col] = oldGrid[row][col];
                    if (field[row][col].hasActor()) actorPos.set(row, col);
                    if (field[row][col].hasStart()) startPos.set(row, col);
                } else {
                    field[row][col] = Field.FREE;
                }
            }
        }
        notifyObservers();
    }

    //__________________________________________________________________________________________________________________
    //    Controllable - placement commands
    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void remove(int row, int col) {
        if (field[row][col].notRemovable()) return;
        field[row][col] = field[row][col].set(Field.FREE);
        notifyObservers();
    }

    @Override
    public void place(Field field, int row, int col) {
        switch (field) {
            case FREE:
                remove(row, col);
                break;
            case ACTOR:
                placeActor(row, col);
                break;
            case ITEM:
                placeItem(row, col);
                break;
            case OBSTACLE:
                placeObstacle(row, col);
                break;
            case START:
                placeStart(row, col);
                break;
        }
    }

    @Override
    public void placeObstacle(int row, int col) throws PositionInvalidException {
        checkBoundary(row, col);
        if (field[row][col].hasObstacle() || field[row][col].notRemovable()) return;
        field[row][col] = field[row][col].set(Field.OBSTACLE);
        notifyObservers();
    }

    @Override
    public void placeItem(int row, int col) throws PositionInvalidException {
        checkBoundary(row, col);
        if (field[row][col].hasItem() || field[row][col].hasStart()) return;
        field[row][col] = field[row][col].set(Field.ITEM);
        notifyObservers();
    }

    @Override
    public void placeActor(int row, int col) throws PositionInvalidException {
        checkBoundary(row, col);
        if (field[row][col].hasActor()) return;

        // remove old position with exists
        if (actorPos.exists()) field[actorPos.row][actorPos.col] = field[actorPos.row][actorPos.col].remove(Field.ACTOR);

        // set new position
        field[row][col] = field[row][col].set(Field.ACTOR);

        // confirm position
        actorPos.set(row, col);

        notifyObservers();
    }

    @Override
    public void placeStart(int row, int col) throws PositionInvalidException {
        checkBoundary(row, col);
        if (field[row][col].hasStart()) return;

        // remove old position with exists
        if (startPos.exists()) field[startPos.row][startPos.col] = field[startPos.row][startPos.col].remove(Field.START);

        // set new position
        field[row][col] = field[row][col].set(Field.START);

        // confirm position
        startPos.set(row, col);

        notifyObservers();
    }


    //__________________________________________________________________________________________________________________
    //    Controllable - test commands
    //------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean isInBoundary(int row, int col){
        return row < sizeRow.get() || col < sizeCol.get();
    }

    @Override
    public void checkBoundary(int row, int col) throws PositionInvalidException {
        if (!isInBoundary(row, col)) throw new PositionInvalidException();
    }

    @Override
    public void existActor() throws RequireActorException {
        if (!actorPos.exists()) throw new RequireActorException();
    }

    @Override
    public void existsStart() throws RequireStartException {
        if (!startPos.exists()) throw new RequireStartException();
    }

    @Override
    public boolean isFieldWithActor(int row, int col) {
        return field[row][col].hasActor();
    }

    @Override
    public boolean isFieldWithStart(int row, int col) {
        return field[row][col].hasStart();
    }

    @Override
    public boolean isFieldAtBorder(int row, int col) {
        return row == 0 || col == 0 || row == sizeRow.get() - 1  || col == sizeCol.get() - 1;
    }

    //__________________________________________________________________________________________________________________
    //    Controllable - getter / setter
    //------------------------------------------------------------------------------------------------------------------

    @Override
    public Field[][] getField() {
        return field;
    }

    @Override
    public int sizeRow() {
        return sizeRow.get();
    }

    @Override
    public IntegerProperty sizeRowProperty() {
        return sizeRow;
    }

    @Override
    public int sizeCol() {
        return sizeCol.get();
    }

    @Override
    public IntegerProperty sizeColProperty() {
        return sizeCol;
    }

    @Override
    public Direction getActorDir() {
        return actorDir;
    }

    // _________________________________________________________________________________________________________________
    //     Interactable - movement commands
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void stepAhead() throws NoClearPathException {
        if (!aheadClear()) throw new NoClearPathException();
        placeActor(actorPos.row + actorDir.row, actorPos.col + actorDir.col);
        notifyObservers();
    }

    @Override
    public void turnRight() {
        actorDir = actorDir.turnRight();
        notifyObservers();
    }

    // _________________________________________________________________________________________________________________
    //     Interactable - complexity reducing commands
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void backToStart() {

    }

    // _________________________________________________________________________________________________________________
    //     Interactable - action commands
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void pickUp() throws ItemNotFoundException, BagFullException {
        if (!foundItem()) throw new ItemNotFoundException();
        if (actorBag == BAG_MAX) throw new BagFullException();
        field[actorPos.row][actorPos.col] = field[actorPos.row][actorPos.col].remove(Field.ITEM);
        actorBag++;
        notifyObservers();
    }

    @Override
    public void dropDown() throws ItemDropNotAllowedException, BagEmptyException {
        if (!atStart()) throw new ItemDropNotAllowedException();
        if (bagEmpty()) throw new BagEmptyException();
        actorBag--;
        notifyObservers();
    }

    // _________________________________________________________________________________________________________________
    //     Interactable - test commands
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean aheadClear() {
        int nextX = actorPos.row + actorDir.row;
        int nextY = actorPos.col + actorDir.col;
        return !field[nextX][nextY].hasObstacle() && isInBoundary(nextX, nextY);
    }

    @Override
    public boolean bagEmpty() {
        return actorBag == 0;
    }

    @Override
    public boolean foundItem() {
        return field[actorPos.row][actorPos.col].equals(Field.ACTOR_ON_ITEM);
    }

    @Override
    public boolean atStart() {
        return field[actorPos.row][actorPos.col].equals(Field.ACTOR_AT_START);
    }
}