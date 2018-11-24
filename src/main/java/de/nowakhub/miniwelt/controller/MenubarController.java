package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.exceptions.InvalidInputException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;


public class MenubarController extends ActionController {

    @FXML
    public ToggleGroup mouseModeToggleGroupMenubar;



    @FXML
    public void onEditorPrint(ActionEvent actionEvent) {
        model.statusText.setValue("onEditorPrint");
    }
    @FXML
    public void onEditorExit(ActionEvent actionEvent) {
        model.statusText.setValue("onEditorExit");
    }



    @FXML
    public void onWorldReset(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldReset");
    }
    @FXML
    public void onWorldLoad(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldLoad");
    }
    @FXML
    public void onWorldSave(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldSave");
    }
    @FXML
    public void onWorldExportPNG(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldExportPNG");
    }
    @FXML
    public void onWorldExportXML(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldExportXML");
    }
    @FXML
    public void onWorldExportText(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldExportText");
    }
    @FXML
    public void onWorldChangeSize(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldChangeSize");
        TextInputDialog dialog = new TextInputDialog(model.world.sizeRow() + "x" + model.world.sizeCol());
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Change dimension of the model.world (Rows x Cols).\nRequire format: [nxn | n e IN]");
        dialog.setContentText("Please enter dimension:");
        dialog.showAndWait().ifPresent(input -> {
            String[] dimension = input.split("x");
            if (!input.matches("\\d+x\\d+") || dimension.length != 2) throw new InvalidInputException("Invalid format for model.world dimension");
            model.world.resize(Integer.valueOf(dimension[0]), Integer.valueOf(dimension[1]));
        });
    }



    @FXML
    public void onActorBagChangeSize(ActionEvent actionEvent) {
        model.statusText.setValue("onActorBagChangeSize");
        TextInputDialog dialog = new TextInputDialog("" + model.world.getActorBagMax());
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Change maximal size of actor bag.\nRequire format: [n | n e IN]");
        dialog.setContentText("Please enter maximal item count:");
        dialog.showAndWait().ifPresent(input -> {
            if (!input.matches("\\d+")) throw new InvalidInputException("Invalid format for maximal bag size");
            model.world.setActorBagMax(Integer.valueOf(input));
        });
    }
    @FXML
    public void onActorStepAhead(ActionEvent actionEvent) {
        model.world.stepAhead();
        model.statusText.setValue("onActorStepAhead");
    }
    @FXML
    public void onActorTurnRight(ActionEvent actionEvent) {
        model.world.turnRight();
        model.statusText.setValue("onActorTurnRight");
    }
    @FXML
    public void onActorBackToStart(ActionEvent actionEvent) {
        model.world.backToStart();
        model.statusText.setValue("onActorBackToStart");
    }
    @FXML
    public void onActorAheadClear(ActionEvent actionEvent) {
        model.statusText.setValue("onActorAheadClear");
    }
    @FXML
    public void onActorBagEmpty(ActionEvent actionEvent) {
        model.statusText.setValue("onActorBagEmpty");
    }
    @FXML
    public void onActorFoundItem(ActionEvent actionEvent) {
        model.statusText.setValue("onActorFoundItem");
    }
    @FXML
    public void onActorPickUp(ActionEvent actionEvent) {
        model.world.pickUp();
        model.statusText.setValue("onActorPickUp");
    }
    @FXML
    public void onActorDropDown(ActionEvent actionEvent) {
        model.world.dropDown();
        model.statusText.setValue("onActorDropDown");
    }

}

