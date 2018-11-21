package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Field;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


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
    public void onActorBagChangeSize(ActionEvent actionEvent) {
        statusText.setValue("onActorBagChangeSize");
    }
    @FXML
    public void onActorBagChangeContent(ActionEvent actionEvent) {
        statusText.setValue("onActorBagChangeContent");
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

