package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.exceptions.NoClearPathException;
import de.nowakhub.miniwelt.exceptions.PositionInvalidException;
import de.nowakhub.miniwelt.exceptions.RequireActorException;
import de.nowakhub.miniwelt.exceptions.RequireStartException;

public class World implements ActorInteraction {

    // limits
    private int max_X;
    private int max_Y;
    
    // has
    private Field[][] field; // or use linked list (drawback: consumes more memory, may be a performance issue)
    private Position actor;
    private Position start;

    public World(int max_X, int max_Y) {
        resize(max_X, max_Y);
    }

    //__________________________________________________________________________________________________________________
    //    commands
    //------------------------------------------------------------------------------------------------------------------

    public Field[][] state() {
        return field.clone();
    }

    public void resize(int max_X, int max_Y) {
        this.max_X = max_X;
        this.max_Y = max_Y;

        Field[][] oldGrid = field;
        field = new Field[max_X][max_Y];
        actor = new Position();
        start = new Position();

        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[x].length; y++) {
                if (oldGrid != null && x < oldGrid.length && y < oldGrid[x].length) {
                    field[x][y] = oldGrid[x][y];
                    if (field[x][y].isOccupiedBy(Field.ACTOR)) actor.set(x, y);
                    if (field[x][y].isOccupiedBy(Field.START)) start.set(x, y);
                } else {
                    field[x][y] = Field.FREE;
                }
            }

        }
    }
    
    public void checkStartExists() throws RequireStartException {
        if (!start.exists()) throw new RequireStartException();
    }

    public void checkActorExists() throws RequireActorException {
        if (!actor.exists()) throw new RequireActorException();
    }

    //__________________________________________________________________________________________________________________
    //    placement commands
    //------------------------------------------------------------------------------------------------------------------

    public void remove(int x, int y) {
        field[x][y] = Field.FREE;
    }

    public void placeWall(int x, int y) throws PositionInvalidException {
        checkIsInBoundary(x, y);
        preparePossibleOverride(x, y);
        field[x][y] = field[x][y].with(Field.OBSTACLE);
    }

    public void placeDanger(int x, int y) throws PositionInvalidException {
        checkIsInBoundary(x, y);
        preparePossibleOverride(x, y);
        field[x][y] = field[x][y].with(Field.ITEM);
    }

    public void placeKarl(int x, int y) throws PositionInvalidException {
        checkIsInBoundary(x, y);

        // remove old position with exists
        if (actor.exists()) field[actor.x][actor.y] = field[actor.x][actor.y].without(Field.ACTOR);

        // set new position
        field[x][y] = field[x][y].with(Field.ACTOR);

        // confirm position
        actor.set(x, y);
    }

    public void placeOffice(int x, int y) throws PositionInvalidException {
        checkIsInBoundary(x, y);
        
        // remove old position with exists
        if (start.exists()) field[start.x][start.y] = field[start.x][start.y].without(Field.START);

        // set new position
        field[x][y] = field[x][y].with(Field.START);

        // confirm position
        start.set(x, y);
    }

    //__________________________________________________________________________________________________________________
    //    check commands
    //------------------------------------------------------------------------------------------------------------------

    private boolean isInBoundary(int x, int y){
        return x < max_X || y < max_Y;
    }

    private void checkIsInBoundary(int x, int y) throws PositionInvalidException {
        if (!isInBoundary(x, y)) throw new PositionInvalidException();
    }

    // _________________________________________________________________________________________________________________
    // utility methods
    // -----------------------------------------------------------------------------------------------------------------

    private void preparePossibleOverride(int x, int y) {
        if (Field.AllOfActor.contains(field[x][y])) {
            actor.set(-1, -1);
        }
        if (Field.AllOfOffice.contains(field[x][y])) {
            start.set(-1, -1);
        }
    }

    // _________________________________________________________________________________________________________________
    // Actor Interactions Implementation
    // -----------------------------------------------------------------------------------------------------------------
    
    @Override
    public void doStepForward(int dirX, int dirY) throws NoClearPathException {
        if (!aheadClear(dirX, dirY)) throw new NoClearPathException();
        actor.x += dirX;
        actor.y += dirY;
        if (foundDanger()) field[actor.x][actor.y] = Field.ACTOR_ON_ITEM;
        else if (inOffice()) field[actor.x][actor.y] = Field.ACTOR_AT_START;
        else field[actor.x][actor.y] = Field.ACTOR;
    }

    @Override
    public boolean aheadClear(int dirX, int dirY) {
        int x = actor.x + dirX;
        int y = actor.y + dirY;
        return !field[x][y].isOccupiedBy(Field.OBSTACLE) && isInBoundary(x, y);
    }

    @Override
    public boolean foundDanger() {
        return field[actor.x][actor.y].isOccupiedBy(Field.ACTOR_ON_ITEM);
    }

    @Override
    public boolean inOffice() {
        return field[actor.x][actor.y].isOccupiedBy(Field.ACTOR_AT_START);
    }
}