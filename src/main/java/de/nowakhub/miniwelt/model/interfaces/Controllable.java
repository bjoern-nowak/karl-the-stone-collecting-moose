package de.nowakhub.miniwelt.model.interfaces;

import de.nowakhub.miniwelt.model.Direction;
import de.nowakhub.miniwelt.model.Field;
import de.nowakhub.miniwelt.model.exceptions.InvalidWorldSizeException;
import de.nowakhub.miniwelt.model.exceptions.PositionInvalidException;
import de.nowakhub.miniwelt.model.exceptions.RequireActorException;
import de.nowakhub.miniwelt.model.exceptions.RequireStartException;

public interface Controllable {

    //__________________________________________________________________________________________________________________
    //    Controllable - commands
    //------------------------------------------------------------------------------------------------------------------

    public void reset();

    public void random();

    void resize(int sizeX, int sizeY) throws InvalidWorldSizeException;

    //__________________________________________________________________________________________________________________
    //    Controllable - placement commands
    //------------------------------------------------------------------------------------------------------------------

    void remove(int x, int y);

    void place(Field field, int x, int y);

    void placeObstacle(int x, int y) throws PositionInvalidException;

    void placeItem(int x, int y) throws PositionInvalidException;

    void placeActor(int x, int y) throws PositionInvalidException;

    void placeStart(int x, int y) throws PositionInvalidException;


    //__________________________________________________________________________________________________________________
    //    Controllable - test commands
    //------------------------------------------------------------------------------------------------------------------

    boolean isInBoundary(int x, int y);

    void checkBoundary(int x, int y) throws PositionInvalidException;

    void existActor() throws RequireActorException;

    void existsStart() throws RequireStartException;

    boolean isFieldWithActor(int x, int y);

    boolean isFieldWithStart(int x, int y);

    boolean isFieldAtBorder(int x, int y);

    //__________________________________________________________________________________________________________________
    //    Controllable - getter / setter
    //------------------------------------------------------------------------------------------------------------------

    Field[][] getField();

    int getSizeRow();

    int getSizeCol();

    Direction getActorDir();

    int getActorBag();

    void setActorBag(int actorBag);

    int getActorBagMax();

    void setActorBagMax(int actorBagMax);
}