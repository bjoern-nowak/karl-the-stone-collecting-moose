package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.controller.TabsController;
import javafx.beans.property.*;
import javafx.scene.canvas.Canvas;

import java.io.File;

public class Model {

    public final TabsController tabsController;

    public File programFile;
    public final StringProperty program = new SimpleStringProperty();
    public final BooleanProperty programDirty = new SimpleBooleanProperty();
    public final BooleanProperty programSaved = new SimpleBooleanProperty();
    public final BooleanProperty programCompiled = new SimpleBooleanProperty();
    public String programSave;

    private World world;
    private Actor actor;
    public final BooleanProperty worldChanged = new SimpleBooleanProperty();
    public final BooleanProperty simulationRunning = new SimpleBooleanProperty();
    public final ObjectProperty<Field> mouseMode = new SimpleObjectProperty<>();

    public Canvas frame;
    public final StringProperty statusText = new SimpleStringProperty();


    public Model(TabsController tabsController, File programFile, String program) {
        this.tabsController = tabsController;
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
        worldChanged.set(true); // forces rootController to re-init observers
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
