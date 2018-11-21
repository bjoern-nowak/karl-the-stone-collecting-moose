package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Field;
import de.nowakhub.miniwelt.model.exceptions.InvalidInputException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;


public abstract class ActionController extends SubController {
    public static enum EXPORT {PNG, XML}



    @FXML
    public void onEditorNew(ActionEvent actionEvent) {
        statusText.setValue("onEditorNew");
    }
    @FXML
    public void onEditorOpen(ActionEvent actionEvent) {
        statusText.setValue("onEditorOpen");
    }
    @FXML
    public void onEditorSave(ActionEvent actionEvent) {
        statusText.setValue("onEditorSave");
    }
    @FXML
    public void onEditorCompile(ActionEvent actionEvent) {
        statusText.setValue("onEditorCompile");
    }



    @FXML
    public void onMouseModePlaceObstacle(ActionEvent actionEvent) {
        mouseMode.setValue(Field.OBSTACLE);
        statusText.setValue("onMouseModePlaceWall");
    }
    @FXML
    public void onMouseModePlaceActor(ActionEvent actionEvent) {
        mouseMode.setValue(Field.ACTOR);
        statusText.setValue("onMouseModePlaceActor");
    }
    @FXML
    public void onMouseModePlaceItem(ActionEvent actionEvent) {
        mouseMode.setValue(Field.ITEM);
        statusText.setValue("onMouseModePlaceItem");
    }
    @FXML
    public void onMouseModePlaceStart(ActionEvent actionEvent) {
        mouseMode.setValue(Field.START);
        statusText.setValue("onMouseModePlaceStart");
    }
    @FXML
    public void onMouseModePlaceFree(ActionEvent actionEvent) {
        mouseMode.setValue(Field.FREE);
        statusText.setValue("onMouseModePlaceFree");
    }




    @FXML
    public void onActorBagChangeContent(ActionEvent actionEvent) {
        statusText.setValue("onActorBagChangeContent");
        TextInputDialog dialog = new TextInputDialog("" + world.get().getActorBag());
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Change item count of actor bag.\nRequire format: [n | n e IN, n <= maximal bag size]");
        dialog.setContentText("Please enter item count smaller then " + (world.get().getActorBagMax()+1) + ":");
        dialog.showAndWait().ifPresent(input -> {
            if (!input.matches("\\d+") || world.get().getActorBagMax() < Integer.valueOf(input))
                throw new InvalidInputException("Invalid format for bag item count");
            world.get().setActorBag(Integer.valueOf(input));
        });
    }





    @FXML
    public void onSimStartOrContinue(ActionEvent actionEvent) {
        statusText.setValue("onSimStartOrContinue");
    }
    @FXML
    public void onSimPause(ActionEvent actionEvent) {
        statusText.setValue("onSimPause");
    }
    @FXML
    public void onSimStop(ActionEvent actionEvent) {
        statusText.setValue("onSimStop");
    }

}

