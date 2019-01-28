package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.model.util.Field;
import de.nowakhub.miniwelt.model.util.Observable;
import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;

import java.io.File;

public class Model extends Observable {

    private World world = new World();
    private Actor actor = new Actor(world);
    public final StringProperty program = new SimpleStringProperty();

    public File programFile;
    public String programSave;
    public final BooleanProperty programDirty = new SimpleBooleanProperty(false);
    public final BooleanProperty programSaved = new SimpleBooleanProperty(false);
    public final BooleanProperty programCompiled = new SimpleBooleanProperty(false);

    public Canvas worldCanvas;
    public final ObjectProperty<Field> mouseMode = new SimpleObjectProperty<>();
    public final BooleanProperty simulationRunning = new SimpleBooleanProperty(false);
    public final IntegerProperty requestOfStudent = new SimpleIntegerProperty(-1);
    public final StringProperty statusText = new SimpleStringProperty();


    // TODO rework this
    public Model(File programFile, String program) {
        this.programFile = programFile;

        this.program.set(program);
        this.programSave = program;

        if (programFile == null && program == null) {
            this.program.set("void main() {\n\tstepAhead();\n}");
            this.programDirty.set(true);
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
}
