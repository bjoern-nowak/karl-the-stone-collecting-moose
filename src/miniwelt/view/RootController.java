package miniwelt.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.*;

public class RootController {


    public enum EXPORT {PNG, XML}

    @FXML
    public Slider sliderSpeed;

    @FXML
    public Label status;
    
    private final String ACTION_PREFIX = "Aktion: ";

    @FXML
    public void onNew(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onNew.");
    }

    @FXML
    public void onOpen(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onOpen.");
    }

    @FXML
    public void onCompile(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onCompile.");
    }

    @FXML
    public void onPrint(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onPrint.");
    }

    @FXML
    public void onExit(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onExit.");
    }

    @FXML
    public void onLoad(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onLoad.");
    }

    @FXML
    public void onSave(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onSave.");
    }

    public void onExport(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onExport.");
    }

    @FXML
    public void onChangeSize(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onChangeSize.");
    }

    @FXML
    public void onPlaceKarl(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onPlaceKarl.");
    }

    @FXML
    public void onPlaceWall(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onPlaceWall.");
    }

    @FXML
    public void onPlaceTable(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onPlaceTable.");
    }

    @FXML
    public void onPlaceObject(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onPlaceObject.");
    }

    @FXML
    public void onChangePocketSize(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onChangePocketSize.");
    }

    @FXML
    public void onSetPocketContent(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onSetPocketContent.");
    }

    @FXML
    public void onStepAhead(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onStepAhead.");
    }

    @FXML
    public void onTurnRIght(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onTurnRIght.");
    }

    @FXML
    public void onBackHome(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onBackHome.");
    }

    @FXML
    public void onIsAheadClear(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onIsAheadClear.");
    }

    @FXML
    public void onIsPocketEmpty(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onIsPocketEmpty.");
    }

    @FXML
    public void onSearchObject(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onSearchObject.");
    }

    @FXML
    public void onPickUp(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onPickUp.");
    }

    @FXML
    public void onDropDown(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onDropDown.");
    }

    @FXML
    public void onStartOrContinue(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onStartOrContinue.");
    }

    @FXML
    public void onPause(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onPause.");
    }

    @FXML
    public void onStop(ActionEvent actionEvent) {
        status.setText(ACTION_PREFIX + "onStop.");
    }

    @FXML
    public void onSpeedClick(MouseEvent mouseEvent) {
        status.setText(ACTION_PREFIX + "Speed changed to " + sliderSpeed.getValue());
    }

    @FXML
    public void onSpeedDrag(MouseEvent mouseEvent) {
        status.setText(ACTION_PREFIX + "Speed is changing...");
    }
}
