package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.controller.util.ModelCtx;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class RootController {

    @FXML
    private Tab tab;
    @FXML
    private Label status;

    @FXML
    private ActionController actionController;
    @FXML
    private TabsController tabsController;

    public RootController() {
        ModelCtx.set(new Model(null, null)); // will be overridden as soon as default tab has been added
    }

    public void initialize(){
        status.textProperty().bindBidirectional(ModelCtx.get().statusText);

        actionController.postInitialize(tabsController);
    }

    public ObservableList<Tab> getTabs() {
        return tabsController.getTabs();
    }
}
