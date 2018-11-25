package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Editor;
import javafx.fxml.FXML;

public class ProgramController extends ModelController {

    @FXML
    private Editor program;

    public void postInitialize() {
        program.setDirty(model.programDirty);

        program.textProperty().bindBidirectional(model.program);
        program.textProperty().addListener(
                (observable, oldValue, newValue) -> program.setDirty(!model.programSave.equals(newValue)));
        program.dirtyProperty().addListener(
                (observable, oldValue, newValue) -> model.programDirty = newValue);
    }
}
