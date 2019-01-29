package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.controller.util.Simulation;
import de.nowakhub.miniwelt.model.util.Field;
import de.nowakhub.miniwelt.model.util.Observable;
import de.nowakhub.miniwelt.view.Editor;
import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;

import java.io.File;

public class Model extends Observable {

    private World world = new World();
    private Actor actor = new Actor(world);
    public final StringProperty program = new SimpleStringProperty();

    public File programFile;
    public String programSave;
    public final ObjectProperty<Editor.STATE> programState = new SimpleObjectProperty<>(Editor.STATE.DIRTY);

    public Simulation simulation;
    public int simulationSpeed;
    public final BooleanProperty simulationRunning = new SimpleBooleanProperty(false);

    public Canvas worldCanvas;
    public final IntegerProperty requestOfStudent = new SimpleIntegerProperty(-1);
    public final ObjectProperty<Field> mouseMode = new SimpleObjectProperty<>();


    // TODO rework this
    public Model(File programFile, String program) {
        this.programFile = programFile;

        this.program.set(program);
        this.programSave = program;

        if (programFile == null && program == null) {
            this.program.set("void main() {\n\tstepAhead();\n}");
            this.programState.set(Editor.STATE.DIRTY);
            this.programSave = null;
        }
    }


    //__________________________________________________________________________________________________________________
    //    convenient getter/setter
    //------------------------------------------------------------------------------------------------------------------

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
        actor.setInteractable(world);
        notifyObservers();
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
        world.notifyObservers();
    }

    public boolean isProgramDirty() {
        return programState.get().equals(Editor.STATE.DIRTY);
    }
    public boolean isProgramCompiled() {
        return programState.get().equals(Editor.STATE.COMPILED);
    }
}
