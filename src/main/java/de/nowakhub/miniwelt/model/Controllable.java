package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.model.exceptions.InvalidWorldSizeException;
import de.nowakhub.miniwelt.model.exceptions.PositionInvalidException;
import de.nowakhub.miniwelt.model.exceptions.RequireActorException;
import de.nowakhub.miniwelt.model.exceptions.RequireStartException;
import javafx.beans.property.IntegerProperty;

public interface Controllable {

    //__________________________________________________________________________________________________________________
    //    Controllable - commands
    //------------------------------------------------------------------------------------------------------------------

    public void resize(int sizeX, int sizeY) throws InvalidWorldSizeException;

    //__________________________________________________________________________________________________________________
    //    Controllable - placement commands
    //------------------------------------------------------------------------------------------------------------------

    public void remove(int x, int y);

    public void place(Field field, int x, int y);

    public void placeObstacle(int x, int y) throws PositionInvalidException;

    public void placeItem(int x, int y) throws PositionInvalidException;

    public void placeActor(int x, int y) throws PositionInvalidException;

    public void placeStart(int x, int y) throws PositionInvalidException;


    //__________________________________________________________________________________________________________________
    //    Controllable - test commands
    //------------------------------------------------------------------------------------------------------------------

    public boolean isInBoundary(int x, int y);

    public void checkBoundary(int x, int y) throws PositionInvalidException;

    public void existActor() throws RequireActorException;

    public void existsStart() throws RequireStartException;

    public boolean isFieldWithActor(int x, int y);

    public boolean isFieldWithStart(int x, int y);

    public boolean isFieldAtBorder(int x, int y);

    //__________________________________________________________________________________________________________________
    //    Controllable - getter / setter
    //------------------------------------------------------------------------------------------------------------------

    public Field[][] getField();

    public int sizeRow();

    public IntegerProperty sizeRowProperty();

    public int sizeCol();

    public IntegerProperty sizeColProperty();

    public Direction getActorDir();

    public int getActorBag();

    public void setActorBag(int actorBag);

    int getActorBagMax();

    void setActorBagMax(int actorBagMax);
}