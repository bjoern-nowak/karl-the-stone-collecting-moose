package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.*;
import de.nowakhub.miniwelt.model.Model;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Controller for primary stage (root)
 */
public class RootController {

    @FXML
    private BorderPane root;

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

        postInitialize();
    }

    /**
     * post initialize other controllers where necessary
     */
    void postInitialize(){
        actionController.postInitialize(this, tabsController);
    }

    /**
     * {@inheritDoc}
     */
    public ObservableList<Tab> getTabs() {
        return tabsController.getTabs();
    }

    /**
     * reload partial fxml (menubar) and re-set action controller
     */
    void reloadActionMenu() throws IOException {
        // load new fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/nowakhub/miniwelt/view/action.fxml"), Message.getBundle());
        Node actionMenu = loader.load();
        root.setTop(actionMenu);

        // get new controller initialized
        actionController = loader.getController();
        postInitialize();
    }
}
