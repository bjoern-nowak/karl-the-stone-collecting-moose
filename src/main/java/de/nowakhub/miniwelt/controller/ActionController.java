package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.MouseMode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;


public class ActionController extends SubController {
    public static final String PREFIX_EVENT = "Event: ";
    public static final String PREFIX_MOUSE_MODE = "Mouse mode: ";
    public static enum EXPORT {PNG, XML}

    @FXML
    public Slider sliderSpeed;


    public void initialize() {
        if (sliderSpeed != null) {
            sliderSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
                statusText.setValue(PREFIX_EVENT + "Speed changed to " + newValue.intValue());
            });
        }
    }

    @FXML
    public void onNew(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onNew.");
    }

    @FXML
    public void onOpen(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onOpen.");
    }

    @FXML
    public void onCompile(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onCompile.");
    }

    @FXML
    public void onPrint(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onPrint.");
    }

    @FXML
    public void onExit(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onExit.");
    }




    @FXML
    public void onReset(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onReset.");
    }

    @FXML
    public void onLoad(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onLoad.");
    }

    @FXML
    public void onSave(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onSave.");
    }

    public void onExport(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onExport.");
    }

    @FXML
    public void onChangeWorldSize(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onChangeSize.");
    }

    @FXML
    public void onPlaceActor(ActionEvent actionEvent) {
        mouseMode.setValue(MouseMode.PLACE_KARL);
        statusText.setValue(PREFIX_MOUSE_MODE + mouseMode);
    }

    @FXML
    public void onPlaceWall(ActionEvent actionEvent) {
        mouseMode.setValue(MouseMode.PLACE_WALL);
        statusText.setValue(PREFIX_MOUSE_MODE + mouseMode);
    }

    @FXML
    public void onPlaceItem(ActionEvent actionEvent) {
        mouseMode.setValue(MouseMode.PLACE_ITEM);
        statusText.setValue(PREFIX_MOUSE_MODE + mouseMode);
    }

    @FXML
    public void onPlaceStart(ActionEvent actionEvent) {
        mouseMode.setValue(MouseMode.PLACE_START);
        statusText.setValue(PREFIX_MOUSE_MODE + mouseMode);
    }

    @FXML
    public void onRemove(ActionEvent actionEvent) {
        mouseMode.setValue(MouseMode.PLACE_FREE);
        statusText.setValue(PREFIX_MOUSE_MODE + mouseMode);
    }




    @FXML
    public void onSetBagSize(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onSetBagSize.");
    }

    @FXML
    public void onSetBagContent(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onSetBagContent.");
    }

    @FXML
    public void onStepAhead(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onStepAhead.");
        actor.get().stepAhead();
    }

    @FXML
    public void onTurnRight(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onTurnRight.");
        actor.get().turnRight();
    }

    @FXML
    public void onBackToStart(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onBackToStart.");
        actor.get().backToStart();
    }

    @FXML
    public void onAheadClear(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "aheadClear: " + actor.get().aheadClear());
    }

    @FXML
    public void onBagEmpty(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "bagEmpty: " + actor.get().bagEmpty());
    }

    @FXML
    public void onFoundItem(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "foundItem: " + actor.get().foundItem());
    }

    @FXML
    public void onPickUp(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onPickUp.");
        actor.get().pickUp();
    }

    @FXML
    public void onDropDown(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onDropDown.");
        actor.get().dropDown();
    }




    @FXML
    public void onStartOrContinue(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onStartOrContinue.");
    }

    @FXML
    public void onPause(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onPause.");
    }

    @FXML
    public void onStop(ActionEvent actionEvent) {
        statusText.setValue(PREFIX_EVENT + "onStop.");
    }

}

