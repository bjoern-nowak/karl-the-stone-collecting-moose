package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.exceptions.NoClearPathException;

public interface ActorInteraction {

    public void doStepForward(int dirX, int dirY) throws NoClearPathException;

    public boolean aheadClear(int dirX, int dirY);

    public boolean foundDanger();
    
    public boolean inOffice();
}
