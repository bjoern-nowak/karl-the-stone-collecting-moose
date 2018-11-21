package de.nowakhub.miniwelt.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;


public class MenubarController extends ActionController {

    @FXML
    public ToggleGroup mouseModeToggleGroupMenubar;



    @FXML
    public void onEditorPrint(ActionEvent actionEvent) {
        statusText.setValue("onEditorPrint");
    }
    @FXML
    public void onEditorExit(ActionEvent actionEvent) {
        statusText.setValue("onEditorExit");
    }



    @FXML
    public void onWorldReset(ActionEvent actionEvent) {
        statusText.setValue("onWorldReset");
    }
    @FXML
    public void onWorldLoad(ActionEvent actionEvent) {
        statusText.setValue("onWorldLoad");
    }
    @FXML
    public void onWorldSave(ActionEvent actionEvent) {
        statusText.setValue("onWorldSave");
    }
    @FXML
    public void onWorldExportPNG(ActionEvent actionEvent) {
        statusText.setValue("onWorldExportPNG");
    }
    @FXML
    public void onWorldExportXML(ActionEvent actionEvent) {
        statusText.setValue("onWorldExportXML");
    }
    @FXML
    public void onWorldExportText(ActionEvent actionEvent) {
        statusText.setValue("onWorldExportText");
    }
    @FXML
    public void onWorldChangeSize(ActionEvent actionEvent) {
        statusText.setValue("onWorldChangeSize");
    }



    @FXML
    public void onActorBagChangeSize(ActionEvent actionEvent) {
        statusText.setValue("onActorBagChangeSize");
    }
    @FXML
    public void onActorStepAhead(ActionEvent actionEvent) {
        world.get().stepAhead();
        statusText.setValue("onActorStepAhead");
    }
    @FXML
    public void onActorTurnRight(ActionEvent actionEvent) {
        world.get().turnRight();
        statusText.setValue("onActorTurnRight");
    }
    @FXML
    public void onActorBackToStart(ActionEvent actionEvent) {
        world.get().backToStart();
        statusText.setValue("onActorBackToStart");
    }
    @FXML
    public void onActorAheadClear(ActionEvent actionEvent) {
        statusText.setValue("onActorAheadClear");
    }
    @FXML
    public void onActorBagEmpty(ActionEvent actionEvent) {
        statusText.setValue("onActorBagEmpty");
    }
    @FXML
    public void onActorFoundItem(ActionEvent actionEvent) {
        statusText.setValue("onActorFoundItem");
    }
    @FXML
    public void onActorPickUp(ActionEvent actionEvent) {
        world.get().pickUp();
        statusText.setValue("onActorPickUp");
    }
    @FXML
    public void onActorDropDown(ActionEvent actionEvent) {
        world.get().dropDown();
        statusText.setValue("onActorDropDown");
    }

}

