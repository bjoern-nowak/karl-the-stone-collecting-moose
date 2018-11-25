package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.model.exceptions.*;

public class Actor  {

    public Interactable interaction;

    public Actor() {
    }

    public Actor(Interactable interaction) {
        this.interaction = interaction;
    }

    public void main() {

    }

    public Interactable getInteraction() {
        return interaction;
    }

    public void setInteraction(Interactable interaction) {
        this.interaction = interaction;
    }

    //__________________________________________________________________________________________________________________
    //    Interactable - movement commands
    //------------------------------------------------------------------------------------------------------------------

    public void stepAhead() throws NoClearPathException {
        interaction.stepAhead();
    }

    public void turnRight() {
        interaction.turnRight();
    }

    //__________________________________________________________________________________________________________________
    //    Interactable - complexity reducing commands
    //------------------------------------------------------------------------------------------------------------------

    public void backToStart() {
        interaction.backToStart();
    }

    //__________________________________________________________________________________________________________________
    //    Interactable - action commands
    //------------------------------------------------------------------------------------------------------------------

    public void pickUp() throws ItemNotFoundException, BagFullException {
        interaction.pickUp();
    }

    public void dropDown() throws ItemDropNotAllowedException, BagEmptyException {
        interaction.dropDown();
    }

    //__________________________________________________________________________________________________________________
    //    Interactable - test commands
    //------------------------------------------------------------------------------------------------------------------

    public boolean aheadClear() {
        return interaction.aheadClear();
    }

    public boolean bagEmpty() {
        return interaction.bagEmpty();
    }

    public boolean foundItem() {
        return interaction.foundItem();
    }

    public boolean atStart() {
        return interaction.atStart();
    }
}