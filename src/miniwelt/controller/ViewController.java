package miniwelt.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import miniwelt.model.Actor;
import miniwelt.model.World;

public class ViewController {

    public static int DEFAULT_WORLD_SIZE = 10;
    public static enum EXPORT {PNG, XML}
    private static final String PREFIX_EVENT = "Event: ";
    private static final String PREFIX_ACTION_MODE = "Event: ";
    private static enum ActionMode{PLACE_FREE, PLACE_WALL, PLACE_ITEM, PLACE_KARL, PLACE_START};

    private World world;
    private Actor actor;

    private ActionMode action;

    @FXML
    public Slider sliderSpeed;

    @FXML
    public Label status;

    public ViewController() {
        world = new World(DEFAULT_WORLD_SIZE, DEFAULT_WORLD_SIZE);
        actor = new Actor(world);
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
        action = ActionMode.PLACE_KARL;
        status.setText(PREFIX_ACTION_MODE + action);
    }

    @FXML
    public void onPlaceWall(ActionEvent actionEvent) {
        action = ActionMode.PLACE_WALL;
        status.setText(PREFIX_ACTION_MODE + action);
    }

    @FXML
    public void onPlaceItem(ActionEvent actionEvent) {
        action = ActionMode.PLACE_ITEM;
        status.setText(PREFIX_ACTION_MODE + action);
    }

    @FXML
    public void onPlaceStart(ActionEvent actionEvent) {
        action = ActionMode.PLACE_START;
        status.setText(PREFIX_ACTION_MODE + action);
    }

    @FXML
    public void onRemove(ActionEvent actionEvent) {
        action = ActionMode.PLACE_FREE;
        status.setText(PREFIX_ACTION_MODE + action);
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
