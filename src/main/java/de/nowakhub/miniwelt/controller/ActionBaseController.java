package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.Message;
import de.nowakhub.miniwelt.controller.util.Sounds;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;

import java.io.IOException;


public abstract class ActionBaseController {

    RootController rootController;
    TabsController tabsController;

    @FXML
    public Button btnToggleMusic;
    @FXML
    public Button btnToggleLanguage;

    public void initialize() {
    }

    void postInitialize(RootController rootController, TabsController tabsController) {
        this.rootController = rootController;
        this.tabsController = tabsController;
    }

    Window getWindow() {
        return btnToggleMusic.getScene().getWindow();
    }

    @FXML
    public void onToggleMusic(ActionEvent actionEvent) {
        if (Sounds.toggleMusic()) btnToggleMusic.setStyle("-fx-background-image: url('/images/icons/volume_mute.png')");
        else btnToggleMusic.setStyle("-fx-background-image: url('/images/icons/volume_full.png')");
    }

    @FXML
    public void onToggleLanguage(ActionEvent actionEvent) throws IOException {
        Message.toggleLanguage();
        // reload fxml for new locale to take in place
        rootController.reloadActionMenu();
    }

}

