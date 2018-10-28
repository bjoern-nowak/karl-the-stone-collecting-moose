package miniwelt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.*;

public class MainController {


    public enum EXPORT {PNG, XML}

    @FXML
    public Slider sliderSpeed;

    @FXML
    public Label status;

    @FXML
    public void onNew(ActionEvent actionEvent) {
        status.setText("Aktion: onNew.");
    }

    @FXML
    public void onOpen(ActionEvent actionEvent) {
        status.setText("Aktion: onOpen.");
    }

    @FXML
    public void onCompile(ActionEvent actionEvent) {
        status.setText("Aktion: onCompile.");
    }

    @FXML
    public void onPrint(ActionEvent actionEvent) {
        status.setText("Aktion: onPrint.");
    }

    @FXML
    public void onExit(ActionEvent actionEvent) {
        status.setText("Aktion: onExit.");
    }

    @FXML
    public void onLoad(ActionEvent actionEvent) {
        status.setText("Aktion: onLoad.");
    }

    @FXML
    public void onSave(ActionEvent actionEvent) {
        status.setText("Aktion: onSave.");
    }

    public void onExport(ActionEvent actionEvent) {
        status.setText("Aktion: onExport.");
    }

    @FXML
    public void onChangeSize(ActionEvent actionEvent) {
        status.setText("Aktion: onChangeSize.");
    }

    @FXML
    public void onPlaceKarl(ActionEvent actionEvent) {
        status.setText("Aktion: onPlaceKarl.");
    }

    @FXML
    public void onPlaceWall(ActionEvent actionEvent) {
        status.setText("Aktion: onPlaceWall.");
    }

    @FXML
    public void onPlaceTable(ActionEvent actionEvent) {
        status.setText("Aktion: onPlaceTable.");
    }

    @FXML
    public void onPlaceObject(ActionEvent actionEvent) {
        status.setText("Aktion: onPlaceObject.");
    }

    @FXML
    public void onChangePocketSize(ActionEvent actionEvent) {
        status.setText("Aktion: onChangePocketSize.");
    }

    @FXML
    public void onSetPocketContent(ActionEvent actionEvent) {
        status.setText("Aktion: onSetPocketContent.");
    }

    @FXML
    public void onStepAhead(ActionEvent actionEvent) {
        status.setText("Aktion: onStepAhead.");
    }

    @FXML
    public void onTurnRIght(ActionEvent actionEvent) {
        status.setText("Aktion: onTurnRIght.");
    }

    @FXML
    public void onBackHome(ActionEvent actionEvent) {
        status.setText("Aktion: onBackHome.");
    }

    @FXML
    public void onIsAheadClear(ActionEvent actionEvent) {
        status.setText("Aktion: onIsAheadClear.");
    }

    @FXML
    public void onIsPocketEmpty(ActionEvent actionEvent) {
        status.setText("Aktion: onIsPocketEmpty.");
    }

    @FXML
    public void onSearchObject(ActionEvent actionEvent) {
        status.setText("Aktion: onSearchObject.");
    }

    @FXML
    public void onPickUp(ActionEvent actionEvent) {
        status.setText("Aktion: onPickUp.");
    }

    @FXML
    public void onDropDown(ActionEvent actionEvent) {
        status.setText("Aktion: onDropDown.");
    }

    @FXML
    public void onStartOrContinue(ActionEvent actionEvent) {
        status.setText("Aktion: onStartOrContinue.");
    }

    @FXML
    public void onPause(ActionEvent actionEvent) {
        status.setText("Aktion: onPause.");
    }

    @FXML
    public void onStop(ActionEvent actionEvent) {
        status.setText("Aktion: onStop.");
    }

    @FXML
    public void a(MouseEvent dragEvent) {
        status.setText("Aktion: " + dragEvent + ".");
    }
    @FXML
    public void m(MouseEvent dragEvent) {
        status.setText("Aktion: " + dragEvent + ".");
    }
    @FXML
    public void s(ScrollEvent dragEvent) {
        status.setText("Aktion: " + sliderSpeed.getValue() + ".");
    }
}
