package de.nowakhub.miniwelt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ProgramController extends SubController {

    @FXML
    private TextArea editor;

    public void postInitialize() {
        if (model.programFile != null) {
            editor.setText(model.programFile.getName());
        } else {
            editor.setText("Defadfgadsfgadsf");
        }
    }
}
