package de.nowakhub.miniwelt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ProgramController extends ModelController {

    @FXML
    private TextArea program;

    public void postInitialize() {
        program.textProperty().bindBidirectional(model.program);
        program.textProperty().addListener(
                (observable, oldValue, newValue) -> model.programDirty = !model.programSave.equals(newValue));
    }
}
