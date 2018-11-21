package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.model.exceptions.*;

public interface Interactable {

    //__________________________________________________________________________________________________________________
    //    Interactable - movement commands
    //------------------------------------------------------------------------------------------------------------------

    public void stepAhead() throws NoClearPathException;

    public void turnRight();

    //__________________________________________________________________________________________________________________
    //    Interactable - complexity reducing commands
    //------------------------------------------------------------------------------------------------------------------

    public void backToStart();

    //__________________________________________________________________________________________________________________
    //    Interactable - action commands
    //------------------------------------------------------------------------------------------------------------------

    public void pickUp() throws ItemNotFoundException, BagFullException;

    public void dropDown() throws ItemDropNotAllowedException, BagEmptyException;

    //__________________________________________________________________________________________________________________
    //    Interactable - test commands
    //------------------------------------------------------------------------------------------------------------------

    public boolean aheadClear();

    public boolean bagEmpty();

    public boolean foundItem();

    public boolean atStart();

}
