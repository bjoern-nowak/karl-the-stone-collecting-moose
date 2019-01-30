package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.view.Editor;
import javafx.fxml.FXML;

/**
 * Controller for the left side of a tab (the program)
 * Contained in a tab; works directly on a model
 */
public class ProgramController {

    @FXML
    private Editor program;

    void postInitialize(Model model) {
        // update editors state on state update
        model.programState.addListener((observable, oldValue, newValue) -> {
            program.setDirty(false);
            program.setSaved(false);
            program.setCompiled(false);
            switch (newValue) {
                case DIRTY:
                    program.setDirty(true);
                    break;
                case SAVED:
                    program.setSaved(true);
                    break;
                case COMPILED:
                    program.setCompiled(true);
                    break;
            }
        });

        // bind models program with editor
        program.textProperty().bindBidirectional(model.program);

        // on change also update state
        program.textProperty().addListener((observable, oldValue, newValue) -> {
            if (model.programSave == null || !model.programSave.equals(newValue))
                model.programState.set(Editor.STATE.DIRTY);
            else
                model.programState.set(Editor.STATE.SAVED);
        });
    }
}
