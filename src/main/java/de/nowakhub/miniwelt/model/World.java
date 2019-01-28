package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.model.exceptions.*;
import de.nowakhub.miniwelt.model.util.*;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class World extends Observable implements Interactable, Serializable {
    static final long serialVersionUID = 1L;

    // limited by
    protected static final int MIN_SIZE = 2;
    protected int sizeRow;
    protected int sizeCol;
    protected int actorBagMax = 3;

    // has
    protected Field[][] field; // or use linked list (drawback: consumes more memory, may be a performance issue)
    protected Position startPos = new Position();
    protected Position actorPos = new Position();
    protected Direction actorDir = Direction.UP;
    protected int actorBag = 0;


    public World() {
        random();
    }

    public World(int sizeRow, int sizeCol) throws InvalidWorldSizeException {
        resize(sizeRow, sizeCol);
    }



    //__________________________________________________________________________________________________________________
    //    World - commands
    //------------------------------------------------------------------------------------------------------------------

    public void reset() {
        resize(sizeRow, sizeCol);
        synchronized (this) {
            for (int row = 0; row < sizeRow; row++)
                for (int col = 0; col < sizeCol; col++)
                    remove(row, col);
            placeStart(2, 2);
            placeActor(2, 2);
        }
        notifyObservers();
    }

    public void random() {
        synchronized (this) {
            sizeRow = ThreadLocalRandom.current().nextInt(3, 16);
            sizeCol = ThreadLocalRandom.current().nextInt(3, 16);
            reset();

            for (int row = 0; row < sizeRow; row++) {
                for (int col = 0; col < sizeCol; col++) {

                    // make it more likly that obstacles spawn near others
                    double obstacleAround = 0.0;
                    if (isInBoundary(row, col - 1) && field[row][col - 1].hasObstacle()) obstacleAround += 0.15;
                    if (isInBoundary(row, col + 1) && field[row][col + 1].hasObstacle()) obstacleAround += 0.15;
                    if (isInBoundary(row - 1, col) && field[row - 1][col].hasObstacle()) obstacleAround += 0.15;
                    if (isInBoundary(row + 1, col) && field[row + 1][col].hasObstacle()) obstacleAround += 0.15;

                    double random = new Random().nextDouble();
                    //if (isFieldAtBorder(row, col)) {
                    //    placeObstacle(row, col);
                    //} else {
                    if (random < 0.05) {
                        placeItem(row, col);
                    } else if (random < (0.2 + obstacleAround)) {
                        placeObstacle(row, col);
                    }
                    //}
                }
            }
        }
        notifyObservers();
    }

    public void resize(int sizeRow, int sizeCol) throws InvalidWorldSizeException {
        if (MIN_SIZE > sizeRow || MIN_SIZE > sizeCol) throw new InvalidWorldSizeException();

        synchronized (this) {
            this.sizeRow = sizeRow;
            this.sizeCol = sizeCol;

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
        }
        notifyObservers();
    }


    //__________________________________________________________________________________________________________________
    //    World - placement commands
    //------------------------------------------------------------------------------------------------------------------

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

    public void remove(int row, int col) {
        synchronized (this) {
            if (field[row][col].notRemovable()) return;
            field[row][col] = field[row][col].set(Field.FREE);
        }
        notifyObservers();
    }

    public void placeObstacle(int row, int col) throws PositionInvalidException {
        checkBoundary(row, col);
        synchronized (this) {
            if (field[row][col].hasObstacle() || field[row][col].notRemovable()) return;
            field[row][col] = field[row][col].set(Field.OBSTACLE);
        }
        notifyObservers();
    }

    public void placeItem(int row, int col) throws PositionInvalidException {
        checkBoundary(row, col);
        synchronized (this) {
            if (field[row][col].hasItem() || field[row][col].hasStart()) return;
            field[row][col] = field[row][col].set(Field.ITEM);
        }
        notifyObservers();
    }

    public void placeActor(int row, int col) throws PositionInvalidException {
        checkBoundary(row, col);
        synchronized (this) {
            if (field[row][col].hasActor()) return;

            // remove old position with exists
            if (actorPos.exists())
                field[actorPos.row][actorPos.col] = field[actorPos.row][actorPos.col].remove(Field.ACTOR);

            // set new position
            field[row][col] = field[row][col].set(Field.ACTOR);

            // confirm position
            actorPos.set(row, col);
        }
        notifyObservers();
    }

    public void placeStart(int row, int col) throws PositionInvalidException {
        checkBoundary(row, col);
        synchronized (this) {
            if (field[row][col].hasStart()) return;

            // remove old position with exists
            if (startPos.exists())
                field[startPos.row][startPos.col] = field[startPos.row][startPos.col].remove(Field.START);

            // set new position
            field[row][col] = field[row][col].set(Field.START);

            // confirm position
            startPos.set(row, col);
        }
        notifyObservers();
    }


    //__________________________________________________________________________________________________________________
    //    World - test commands
    //------------------------------------------------------------------------------------------------------------------

    public boolean isInBoundary(int row, int col){
        return 0 <= row && row < sizeRow && 0 <= col && col < sizeCol;
    }

    public void checkBoundary(int row, int col) throws PositionInvalidException {
        if (!isInBoundary(row, col)) throw new PositionInvalidException();
    }

    public void existActor() throws RequireActorException {
        if (!actorPos.exists()) throw new RequireActorException();
    }

    public void existsStart() throws RequireStartException {
        if (!startPos.exists()) throw new RequireStartException();
    }

    public boolean isFieldWithActor(int row, int col) {
        return field[row][col].hasActor();
    }

    public boolean isFieldWithStart(int row, int col) {
        return field[row][col].hasStart();
    }

    public boolean isFieldAtBorder(int row, int col) {
        return row == 0 || col == 0 || row == sizeRow - 1  || col == sizeCol - 1;
    }


    // _________________________________________________________________________________________________________________
    //     World - getter/setter
    // -----------------------------------------------------------------------------------------------------------------

    public Field[][] getField() {
        // TODO clone 2d array into new one; make it unmodifyable
        return field;
    }

    public Field getField(int row, int col) {
        return field[row][col];
    }

    public int getSizeRow() {
        return sizeRow;
    }

    public int getSizeCol() {
        return sizeCol;
    }

    public Direction getActorDir() {
        return actorDir;
    }

    public int getActorBagMax() {
        return actorBagMax;
    }

    public void setActorBagMax(int actorBagMax) {
        this.actorBagMax = actorBagMax;
    }

    public int getActorBag() {
        return actorBag;
    }

    public void setActorBag(int actorBag) {
        this.actorBag = actorBag;
    }



    // _________________________________________________________________________________________________________________
    //     Interactable - movement commands
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void stepAhead() throws NoClearPathException {
        synchronized (this) {
            if (!aheadClear()) throw new NoClearPathException();
            placeActor(actorPos.row + actorDir.row, actorPos.col + actorDir.col);
        }
        notifyObservers();
    }

    @Override
    public void turnRight() {
        synchronized (this) {
            actorDir = actorDir.turnRight();
        }
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
        synchronized (this) {
            if (!foundItem()) throw new ItemNotFoundException();
            if (actorBag == actorBagMax) throw new BagFullException();
            field[actorPos.row][actorPos.col] = field[actorPos.row][actorPos.col].remove(Field.ITEM);
            actorBag++;
        }
        notifyObservers();
    }

    @Override
    public void dropDown() throws ItemDropNotAllowedException, BagEmptyException {
        synchronized (this) {
            if (!atStart()) throw new ItemDropNotAllowedException();
            if (bagEmpty()) throw new BagEmptyException();
            actorBag--;
        }
        notifyObservers();
    }


    // _________________________________________________________________________________________________________________
    //     Interactable - test commands
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean aheadClear() {
        int nextX = actorPos.row + actorDir.row;
        int nextY = actorPos.col + actorDir.col;
        return isInBoundary(nextX, nextY) && !field[nextX][nextY].hasObstacle();
    }

    @Override
    public boolean bagEmpty() {
        return actorBag == 0;
    }

    @Override
    public boolean bagFull() {
        return actorBag == actorBagMax;
    }

    @Override
    public boolean foundItem() {
        return field[actorPos.row][actorPos.col].equals(Field.ACTOR_ON_ITEM);
    }

    @Override
    public boolean atStart() {
        return field[actorPos.row][actorPos.col].equals(Field.ACTOR_AT_START);
    }



    // _________________________________________________________________________________________________________________
    //     getter/setter for export/import (XML)
    // -----------------------------------------------------------------------------------------------------------------

    public void setSizeRow(int sizeRow) {
        this.sizeRow = sizeRow;
    }

    public void setSizeCol(int sizeCol) {
        this.sizeCol = sizeCol;
    }

    public void setField(Field[][] field) {
        this.field = field;
    }

    public Position getStartPos() {
        return startPos;
    }

    public void setStartPos(Position startPos) {
        this.startPos = startPos;
    }

    public Position getActorPos() {
        return actorPos;
    }

    public void setActorPos(Position actorPos) {
        this.actorPos = actorPos;
    }

    public void setActorDir(Direction actorDir) {
        this.actorDir = actorDir;
    }
}