package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.view.Editor;
import javafx.fxml.FXML;

public class ProgramController extends ModelController {

    @FXML
    private Editor program;

    public void postInitialize() {
        program.dirtyProperty().bindBidirectional(model.programDirty);
        program.compiledProperty().bindBidirectional(model.programCompiled);

        program.textProperty().bindBidirectional(model.program);
        program.textProperty().addListener(
                (obs, oldV, newV) -> program.setDirty(!model.programSave.equals(newV)));
    }
}
