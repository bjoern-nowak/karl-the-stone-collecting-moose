package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.*;
import de.nowakhub.miniwelt.model.Model;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

/**
 * Controller for primary stage (root)
 */
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
        // will be overridden as soon as default tab has been added
        ModelCtx.set(new Model(null, null));
    }

    public void initialize(){
        // start playing background music
        Sounds.toggleMusic();

        // start or connect to tutor server
        if (PropsCtx.hasServer())
            if (PropsCtx.isTutor())
                Tutor.start();
            else
                Student.connect();

        // bind fxml text to model property
        status.textProperty().bindBidirectional(ModelCtx.get().statusText);

        // post initialize other controllers where necessary
        actionController.postInitialize(tabsController);
    }

    /**
     * passthrough to {@link TabsController#getTabs()}
     */
    public ObservableList<Tab> getTabs() {
        return tabsController.getTabs();
    }
}
