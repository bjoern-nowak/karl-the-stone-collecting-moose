package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.view.Editor;
import javafx.fxml.FXML;

public class ProgramController {

    @FXML
    private Editor program;

    void postInitialize(Model model) {
        model.programState.addListener((observable, oldValue, newValue) -> {
            program.dirtyProperty().set(false);
            program.savedProperty().set(false);
            program.compiledProperty().set(false);
            switch (newValue) {
                case DIRTY:
                    program.dirtyProperty().set(true);
                    break;
                case SAVED:
                    program.savedProperty().set(true);
                    break;
                case COMPILED:
                    program.compiledProperty().set(true);
                    break;
            }
        });

        program.textProperty().bindBidirectional(model.program);
        program.textProperty().addListener(
                (obs, oldV, newV) -> program.setDirty(model.programSave == null || !model.programSave.equals(newV)));
    }
}
