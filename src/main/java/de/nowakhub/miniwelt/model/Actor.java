package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.controller.util.Simulation;
import de.nowakhub.miniwelt.model.exceptions.*;
import de.nowakhub.miniwelt.model.util.Interactable;
import de.nowakhub.miniwelt.model.util.Invisible;

public class Actor {

    private Interactable interactable;

    public Actor() {
    }

    public Actor(Interactable interactable) {
        this.interactable = interactable;
    }

    public void main() {

    }

    // _________________________________________________________________________________________________________________
    //     Interactable - movement commands
    // -----------------------------------------------------------------------------------------------------------------

    public void stepAhead() throws NoClearPathException {
        if (delay()) interactable.stepAhead();
    }

    public void turnRight() {
        if (delay()) interactable.turnRight();
    }

    // _________________________________________________________________________________________________________________
    //     Interactable - complexity reducing commands
    // -----------------------------------------------------------------------------------------------------------------

    public void backToStart() {
        delay();
        // TODO teleport to start
    }

    // _________________________________________________________________________________________________________________
    //     Interactable - action commands
    // -----------------------------------------------------------------------------------------------------------------

    public void pickUp() throws ItemNotFoundException, BagFullException {
        if (delay()) interactable.pickUp();
    }

    public void dropDown() throws ItemDropNotAllowedException, BagEmptyException {
        if (delay()) interactable.dropDown();
    }

    // _________________________________________________________________________________________________________________
    //     Interactable - test commands
    // -----------------------------------------------------------------------------------------------------------------

    public boolean aheadClear() {
        return interactable.aheadClear();
    }

    public boolean bagEmpty() {
        return interactable.bagEmpty();
    }

    public boolean bagFull() {
        return interactable.bagFull();
    }

    public boolean foundItem() {
        return interactable.foundItem();
    }

    public boolean atStart() {
        return interactable.atStart();
    }


    //__________________________________________________________________________________________________________________
    //    invisible commands
    //------------------------------------------------------------------------------------------------------------------

    @Invisible
    public Interactable getInteractable() {
        return interactable;
    }

    @Invisible
    public void setInteractable(Interactable operator) {
        this.interactable = operator;
    }

    @Invisible
    private boolean delay() {
        if (Thread.currentThread() instanceof Simulation)
            return ((Simulation) Thread.currentThread()).delay();
        else
            return true;
    }
}