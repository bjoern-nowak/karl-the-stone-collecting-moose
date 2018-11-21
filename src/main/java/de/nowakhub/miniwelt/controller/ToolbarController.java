package de.nowakhub.miniwelt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;


public class ToolbarController extends ActionController {

    @FXML
    public ToggleGroup mouseModeToggleGroupToolbar;

    @FXML
    public Slider sliderSimSpeed;


    public void initialize() {
        // TODO bind toggle groups
        if (sliderSimSpeed != null) {
            sliderSimSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
                statusText.setValue("Speed changed to " + newValue.intValue());
            });
        }
    }

}

