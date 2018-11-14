package de.nowakhub.miniwelt.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.World;
import de.nowakhub.miniwelt.service.MouseMode;


public class ActionController {
    public static final String PREFIX_EVENT = "Event: ";
    public static final String PREFIX_MOUSE_MODE = "Mouse mode: ";
    public static enum EXPORT {PNG, XML}

    private World world;
    private Actor actor;

    private MouseMode mouseMode;

    @FXML
    public Slider sliderSpeed;

    @FXML
    public Label status;


    public ActionController(World world, Actor actor) {
        this.world = world;
        this.actor = actor;
    }

    @FXML
    public void onNew(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onNew.");
    }

    @FXML
    public void onOpen(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onOpen.");
    }

    @FXML
    public void onCompile(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onCompile.");
    }

    @FXML
    public void onPrint(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onPrint.");
    }

    @FXML
    public void onExit(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onExit.");
    }




    @FXML
    public void onReset(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onReset.");
    }

    @FXML
    public void onLoad(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onLoad.");
    }

    @FXML
    public void onSave(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onSave.");
    }

    public void onExport(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onExport.");
    }

    @FXML
    public void onChangeWorldSize(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onChangeSize.");
    }

    @FXML
    public void onPlaceActor(ActionEvent actionEvent) {
        mouseMode = MouseMode.PLACE_KARL;
        status.setText(PREFIX_MOUSE_MODE + mouseMode);
    }

    @FXML
    public void onPlaceWall(ActionEvent actionEvent) {
        mouseMode = MouseMode.PLACE_WALL;
        status.setText(PREFIX_MOUSE_MODE + mouseMode);
    }

    @FXML
    public void onPlaceItem(ActionEvent actionEvent) {
        mouseMode = MouseMode.PLACE_ITEM;
        status.setText(PREFIX_MOUSE_MODE + mouseMode);
    }

    @FXML
    public void onPlaceStart(ActionEvent actionEvent) {
        mouseMode = MouseMode.PLACE_START;
        status.setText(PREFIX_MOUSE_MODE + mouseMode);
    }

    @FXML
    public void onRemove(ActionEvent actionEvent) {
        mouseMode = MouseMode.PLACE_FREE;
        status.setText(PREFIX_MOUSE_MODE + mouseMode);
    }




    @FXML
    public void onSetBagSize(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onSetBagSize.");
    }

    @FXML
    public void onSetBagContent(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onSetBagContent.");
    }

    @FXML
    public void onStepAhead(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onStepAhead.");
        actor.stepAhead();
    }

    @FXML
    public void onTurnRight(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onTurnRight.");
        actor.turnRight();
    }

    @FXML
    public void onBackToStart(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onBackToStart.");
        actor.backToStart();
    }

    @FXML
    public void onAheadClear(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "aheadClear: " + actor.aheadClear());
    }

    @FXML
    public void onBagEmpty(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "bagEmpty: " + actor.bagEmpty());
    }

    @FXML
    public void onFoundItem(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "foundItem: " + actor.foundItem());
    }

    @FXML
    public void onPickUp(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onPickUp.");
        actor.pickUp();
    }

    @FXML
    public void onDropDown(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onDropDown.");
        actor.dropDown();
    }




    @FXML
    public void onStartOrContinue(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onStartOrContinue.");
    }

    @FXML
    public void onPause(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onPause.");
    }

    @FXML
    public void onStop(ActionEvent actionEvent) {
        status.setText(PREFIX_EVENT + "onStop.");
    }

    @FXML
    public void onSpeedClick(MouseEvent mouseEvent) {
        status.setText(PREFIX_EVENT + "Speed changed to " + sliderSpeed.getValue());
    }

    @FXML
    public void onSpeedDrag(MouseEvent mouseEvent) {
        status.setText(PREFIX_EVENT + "Speed is changing...");
    }
}

