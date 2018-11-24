package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Field;
import de.nowakhub.miniwelt.model.exceptions.InvalidInputException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;


public abstract class ActionController extends SubController {
    public static enum EXPORT {PNG, XML}

    @FXML
    public void onProgramNew(ActionEvent actionEvent) {
        model.statusText.setValue("onProgramNew");
        model.tabsController.add();
    }
    @FXML
    public void onProgramOpen(ActionEvent actionEvent) {
        model.statusText.setValue("onProgramOpen");
        model.tabsController.open();
    }
    @FXML
    public void onProgramSave(ActionEvent actionEvent) {
        model.statusText.setValue("onProgramSave");


    }
    @FXML
    public void onProgramCompile(ActionEvent actionEvent) {
        model.statusText.setValue("onProgramCompile");
    }



    @FXML
    public void onMouseModePlaceObstacle(ActionEvent actionEvent) {
        model.mouseMode.setValue(Field.OBSTACLE);
        model.statusText.setValue("onMouseModePlaceWall");
    }
    @FXML
    public void onMouseModePlaceActor(ActionEvent actionEvent) {
        model.mouseMode.setValue(Field.ACTOR);
        model.statusText.setValue("onMouseModePlaceActor");
    }
    @FXML
    public void onMouseModePlaceItem(ActionEvent actionEvent) {
        model.mouseMode.setValue(Field.ITEM);
        model.statusText.setValue("onMouseModePlaceItem");
    }
    @FXML
    public void onMouseModePlaceStart(ActionEvent actionEvent) {
        model.mouseMode.setValue(Field.START);
        model.statusText.setValue("onMouseModePlaceStart");
    }
    @FXML
    public void onMouseModePlaceFree(ActionEvent actionEvent) {
        model.mouseMode.setValue(Field.FREE);
        model.statusText.setValue("onMouseModePlaceFree");
    }




    @FXML
    public void onActorBagChangeContent(ActionEvent actionEvent) {
        model.statusText.setValue("onActorBagChangeContent");
        TextInputDialog dialog = new TextInputDialog("" + model.world.getActorBag());
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Change item count of actor bag.\nRequire format: [n | n e IN, n <= maximal bag size]");
        dialog.setContentText("Please enter item count smaller then " + (model.world.getActorBagMax()+1) + ":");
        dialog.showAndWait().ifPresent(input -> {
            if (!input.matches("\\d+") || model.world.getActorBagMax() < Integer.valueOf(input))
                throw new InvalidInputException("Invalid format for bag item count");
            model.world.setActorBag(Integer.valueOf(input));
        });
    }





    @FXML
    public void onSimStartOrContinue(ActionEvent actionEvent) {
        model.statusText.setValue("onSimStartOrContinue");
    }
    @FXML
    public void onSimPause(ActionEvent actionEvent) {
        model.statusText.setValue("onSimPause");
    }
    @FXML
    public void onSimStop(ActionEvent actionEvent) {
        model.statusText.setValue("onSimStop");
    }

}

