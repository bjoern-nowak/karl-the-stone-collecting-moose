package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.Field;
import de.nowakhub.miniwelt.model.World;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class SubController {

    final ObjectProperty<World> world = new SimpleObjectProperty<>();
    final ObjectProperty<Actor> actor = new SimpleObjectProperty<>();
    final StringProperty statusText = new SimpleStringProperty();
    final ObjectProperty<Field> mouseMode = new SimpleObjectProperty<>();

    public World getWorld() {
        return world.get();
    }

    public ObjectProperty<World> worldProperty() {
        return world;
    }

    public void setWorld(World world) {
        this.world.set(world);
    }

    public Actor getActor() {
        return actor.get();
    }

    public ObjectProperty<Actor> actorProperty() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor.set(actor);
    }

    public String getStatusText() {
        return statusText.get();
    }

    public StringProperty statusTextProperty() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText.set(statusText);
    }

    public Field getMouseMode() {
        return mouseMode.get();
    }

    public ObjectProperty<Field> mouseModeProperty() {
        return mouseMode;
    }

    public void setMouseMode(Field mouseMode) {
        this.mouseMode.set(mouseMode);
    }
}
