package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import javafx.fxml.FXML;

import java.io.File;

/**
 * Controller for passing initial values from {@link TabsController#addTab(File, Model)} to controllers contained in a tab
 */
public class TabController {

    @FXML
    private ProgramController programController;
    @FXML
    private WorldController worldController;


    void postInitialize(Model model){
        programController.postInitialize(model);
        worldController.postInitialize(model);
    }
}
