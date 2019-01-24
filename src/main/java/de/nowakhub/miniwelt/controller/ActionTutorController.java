package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.ModelCtx;
import de.nowakhub.miniwelt.controller.util.PropsCtx;
import de.nowakhub.miniwelt.model.interfaces.Observer;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;


public abstract class ActionTutorController extends ActionExampleController implements Observer {

    private ChangeListener<Boolean> miTutorListener;


    @FXML
    public Menu miTutor;
    @FXML
    public MenuItem miTutorRequestSend;
    @FXML
    public MenuItem miTutorAnswerLoad;
    @FXML
    public MenuItem miTutorRequestLoad;
    @FXML
    public MenuItem miTutorAnswerSend;


    public ActionTutorController() {
        miTutorListener = (obs, oldV, newV) -> {
            miTutorRequestSend.setDisable(newV);
            miTutorAnswerLoad.setDisable(!newV);
            miTutorRequestLoad.setDisable(newV);
            miTutorAnswerSend.setDisable(!newV);
        };
    }

    public void initialize() {
        super.initialize();

        ModelCtx.addObserver("actionTutor", this);

        update();

        if (!PropsCtx.hasServer()) miTutor.setDisable(true);
        if (PropsCtx.isTutor()) {
            miTutorRequestSend.setVisible(false);
            miTutorAnswerLoad.setVisible(false);
        } else {
            miTutorRequestLoad.setVisible(false);
            miTutorAnswerSend.setVisible(false);
        }
    }

    @Override
    public void update() {
        ModelCtx.get().tutorRequestSendedOrLoaded.removeListener(miTutorListener);
        ModelCtx.get().tutorRequestSendedOrLoaded.addListener(miTutorListener);
        miTutorListener.changed(null, null, ModelCtx.get().tutorRequestSendedOrLoaded.get());
    }



    @FXML
    public void onTutorRequestSend(ActionEvent actionEvent) {
        // TODO
        ModelCtx.get().tutorRequestSendedOrLoaded.set(true);
    }

    @FXML
    public void onTutorAnswerLoad(ActionEvent actionEvent) {
        // TODO
        ModelCtx.get().tutorRequestSendedOrLoaded.set(false);
    }

    @FXML
    public void onTutorRequestLoad(ActionEvent actionEvent) {
        // TODO
        ModelCtx.get().tutorRequestSendedOrLoaded.set(true);
    }

    @FXML
    public void onTutorAnswerSend(ActionEvent actionEvent) {
        // TODO
        ModelCtx.get().tutorRequestSendedOrLoaded.set(false);
    }
}

