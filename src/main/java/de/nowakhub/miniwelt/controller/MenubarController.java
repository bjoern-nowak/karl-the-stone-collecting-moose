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
        TextInputDialog dialog = new TextInputDialog(world.get().sizeRow() + "x" + world.get().sizeCol());
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Change dimension of the world (Rows x Cols).\nRequire format: [nxn | n e IN]");
        dialog.setContentText("Please enter dimension:");
        dialog.showAndWait().ifPresent(input -> {
            String[] dimension = input.split("x");
            if (!input.matches("\\d+x\\d+") || dimension.length != 2) throw new InvalidInputException("Invalid format for world dimension");
            world.get().resize(Integer.valueOf(dimension[0]), Integer.valueOf(dimension[1]));
        });
    }



    @FXML
    public void onActorBagChangeSize(ActionEvent actionEvent) {
        statusText.setValue("onActorBagChangeSize");
        TextInputDialog dialog = new TextInputDialog("" + world.get().getActorBagMax());
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Change maximal size of actor bag.\nRequire format: [n | n e IN]");
        dialog.setContentText("Please enter maximal item count:");
        dialog.showAndWait().ifPresent(input -> {
            if (!input.matches("\\d+")) throw new InvalidInputException("Invalid format for maximal bag size");
            world.get().setActorBagMax(Integer.valueOf(input));
        });
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

