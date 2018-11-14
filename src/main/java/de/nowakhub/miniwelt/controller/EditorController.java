package de.nowakhub.miniwelt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.World;

public class EditorController {

    private World world;
    private Actor actor;

    @FXML
    public Label status;

    public EditorController(World world, Actor actor) {
        this.world = world;
        this.actor = actor;
    }
}
