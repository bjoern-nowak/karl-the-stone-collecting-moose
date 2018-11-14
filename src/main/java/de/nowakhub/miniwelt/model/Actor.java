package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.exceptions.*;

public class Actor {

    // limits
    public static int BAG_MAX = 3;

    // has
    private int bag = 0;
    public ActorDirection dir = ActorDirection.UP;

    // can
    public ActorInteraction interaction;

    public Actor(ActorInteraction interaction) {
        this.interaction = interaction;
    }

    //__________________________________________________________________________________________________________________
    //    movement commands
    //------------------------------------------------------------------------------------------------------------------

    public void stepAhead() throws NoClearPathException {
        interaction.doStepForward(dir.x, dir.y);
    }

    public void turnRight() {
        switch (dir) {
            case UP: dir = ActorDirection.RIGHT;
            case DOWN: dir = ActorDirection.LEFT;
            case LEFT: dir = ActorDirection.UP;
            case RIGHT: dir = ActorDirection.DOWN;
        }
    }

    //__________________________________________________________________________________________________________________
    //    complexity reducing commands
    //------------------------------------------------------------------------------------------------------------------

    public void backToStart() {

    }

    //__________________________________________________________________________________________________________________
    //    action commands
    //------------------------------------------------------------------------------------------------------------------

    public void pickUp() throws ItemNotFoundException, BagFullException {
        if (!foundItem()) throw new ItemNotFoundException();
        if (bag == BAG_MAX) throw new BagFullException();
        bag++;
    }

    public void dropDown() throws ItemDropNotAllowedException, BagEmptyException {
        if (!interaction.inOffice()) throw new ItemDropNotAllowedException();
        if (bagEmpty()) throw new BagEmptyException();
        bag--;
    }

    //__________________________________________________________________________________________________________________
    //    test commands
    //------------------------------------------------------------------------------------------------------------------

    public boolean aheadClear() {
        return interaction.aheadClear(dir.x, dir.y);
    }

    public boolean bagEmpty() {
        return bag == 0;
    }

    public boolean foundItem() {
        return interaction.foundDanger();
    }

}