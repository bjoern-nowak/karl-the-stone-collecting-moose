package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import javafx.fxml.FXML;

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
