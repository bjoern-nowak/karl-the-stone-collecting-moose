package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.view.Editor;
import javafx.fxml.FXML;

public class ProgramController {

    @FXML
    private Editor program;

    public void postInitialize(Model model) {
        program.dirtyProperty().bindBidirectional(model.programDirty);
        program.compiledProperty().bindBidirectional(model.programCompiled);

        program.textProperty().bindBidirectional(model.program);
        program.textProperty().addListener(
                (obs, oldV, newV) -> program.setDirty(model.programSave == null || !model.programSave.equals(newV)));
    }
}
