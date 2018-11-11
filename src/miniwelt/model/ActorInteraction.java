package miniwelt.model;

import miniwelt.exceptions.NoClearPathException;

public interface ActorInteraction {

    public void doStepForward(int dirX, int dirY) throws NoClearPathException;

    public boolean aheadClear(int dirX, int dirY);

    public boolean foundDanger();
    
    public boolean inOffice();
}
