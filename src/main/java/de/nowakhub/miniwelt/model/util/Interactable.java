package de.nowakhub.miniwelt.model.util;

import de.nowakhub.miniwelt.model.exceptions.*;

/**
 * Commands used {@link de.nowakhub.miniwelt.model.Actor} to interact with a {@link de.nowakhub.miniwelt.model.World}
 */
public interface Interactable {

    //__________________________________________________________________________________________________________________
    //    movement commands
    //------------------------------------------------------------------------------------------------------------------

    public void stepAhead() throws NoClearPathException;

    public void turnRight();

    //__________________________________________________________________________________________________________________
    //    complexity reducing commands
    //------------------------------------------------------------------------------------------------------------------

    public void backToStart();

    //__________________________________________________________________________________________________________________
    //    action commands
    //------------------------------------------------------------------------------------------------------------------

    public void pickUp() throws ItemNotFoundException, BagFullException;

    public void dropDown() throws ItemDropNotAllowedException, BagEmptyException;

    //__________________________________________________________________________________________________________________
    //    test commands
    //------------------------------------------------------------------------------------------------------------------

    public boolean aheadClear();

    public boolean bagEmpty();

    public boolean bagFull();

    public boolean foundItem();

    public boolean atStart();

}
