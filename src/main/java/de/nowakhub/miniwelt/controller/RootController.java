package de.nowakhub.miniwelt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.World;

public class RootController {

    private World world;
    private Actor actor;

    @FXML
    public Slider sliderSpeed;

    @FXML
    public Label status;

    public RootController(World world, Actor actor) {
        this.world = world;
        this.actor = actor;
    }
}
