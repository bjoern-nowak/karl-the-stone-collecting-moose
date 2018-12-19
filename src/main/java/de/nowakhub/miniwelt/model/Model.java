package de.nowakhub.miniwelt.model;

import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;

import java.io.File;

public class Model {

    public File programFile;
    public String programSave;
    public final StringProperty program = new SimpleStringProperty();
    public final BooleanProperty programDirty = new SimpleBooleanProperty();
    public final BooleanProperty programSaved = new SimpleBooleanProperty();
    public final BooleanProperty programCompiled = new SimpleBooleanProperty();

    private World world;
    private Actor actor;
    public Canvas worldCanvas;
    public final BooleanProperty simulationRunning = new SimpleBooleanProperty();
    public final ObjectProperty<Field> mouseMode = new SimpleObjectProperty<>();

    public final StringProperty statusText = new SimpleStringProperty();


    public Model(File programFile, String program) {
        this.programFile = programFile;

        this.program.set(program);
        this.programDirty.set(false);
        this.programSave = program;
        if (programFile == null && program == null) {
            this.program.set("void main() {\n\tstepAhead();\n}");
            this.programDirty.set(true);
            this.programSave = null;
        }

        world = new World();
        actor = new Actor(world);
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
        actor.setInteraction(world);
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
        world.notifyObservers();
    }
}
