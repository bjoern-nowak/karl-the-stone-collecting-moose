package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.Sounds;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Window;


public abstract class ActionBaseController {

    TabsController tabsController;

    @FXML
    public Button btnMusicToogle;

    public void initialize() {
    }

    void postInitialize(TabsController tabsController) {
        this.tabsController = tabsController;
    }

    Window getWindow() {
        return btnMusicToogle.getScene().getWindow();
    }

    @FXML
    public void onMusicToogle(ActionEvent actionEvent) {
        if (Sounds.toggleMusic()) btnMusicToogle.setStyle("-fx-background-image: url('/images/icons/volume_full.png')");
        else btnMusicToogle.setStyle("-fx-background-image: url('/images/icons/volume_mute.png')");
    }

}

