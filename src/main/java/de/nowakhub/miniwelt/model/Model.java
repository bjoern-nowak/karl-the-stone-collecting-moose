package de.nowakhub.miniwelt.model;

import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;

import java.io.File;

public class Model {

    public File programFile;
    public String programSave;
    public final StringProperty program = new SimpleStringProperty();
    public final BooleanProperty programDirty = new SimpleBooleanProperty(false);
    public final BooleanProperty programSaved = new SimpleBooleanProperty(false);
    public final BooleanProperty programCompiled = new SimpleBooleanProperty(false);

    public final ObjectProperty<World> world = new SimpleObjectProperty<>(new World());
    private Actor actor = new Actor(world.get());
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
        return world.get();
    }

    public void setWorld(World world) {
        this.world.set(world);
        actor.setInteraction(world);
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
        world.get().notifyObservers();
    }

    public String getProgram() {
        return program.get();
    }

    public void setProgram(String program) {
        this.program.set(program);
    }
}
