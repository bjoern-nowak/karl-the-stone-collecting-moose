package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.*;
import de.nowakhub.miniwelt.model.Model;
import javafx.beans.property.IntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * implements all actions possible through menu of tutor
 */
public abstract class ActionTutorController extends ActionExampleController {

    @FXML
    public Menu miTutor;
    @FXML
    public MenuItem miTutorRequestSend;
    @FXML
    public MenuItem miTutorRequestCancel;
    @FXML
    public MenuItem miTutorAnswerLoad;
    @FXML
    public MenuItem miTutorRequestLoad;
    @FXML
    public MenuItem miTutorAnswerCancel;
    @FXML
    public MenuItem miTutorAnswerSend;



    public void initialize() {
        super.initialize();

        // disable menu till started or connected to server
        miTutor.setDisable(true);
        Tutor.startedProperty().addListener((observable, oldValue, newValue) -> miTutor.setDisable(false));
        Student.connectedProperty().addListener((observable, oldValue, newValue) -> miTutor.setDisable(false));


        // build menu(items) based on properties
        if (PropsCtx.isTutor()) {
            miTutorRequestSend.setVisible(false);
            miTutorRequestCancel.setVisible(false);
            miTutorAnswerLoad.setVisible(false);
        } else {
            miTutorRequestLoad.setVisible(false);
            miTutorAnswerCancel.setVisible(false);
            miTutorAnswerSend.setVisible(false);
        }

        // watch for tab changes (which means a model change) and hook up listener
        ModelCtx.addObserver("actionTutor", this::update);

        // initialize view based on current model
        update();
    }

    private void update() {
        IntegerProperty requestOfStudent = ModelCtx.get().requestOfStudent;

        // if revisiting a tab, remove old listener (avoid stacking listener)
        requestOfStudent.removeListener((observable, oldValue, newValue) -> update());
        requestOfStudent.addListener((observable, oldValue, newValue) -> update());

        // alternate menu items for both roles
        if (PropsCtx.isTutor()) {
            miTutorAnswerCancel.setDisable(requestOfStudent.get() == -1);
            miTutorAnswerSend.setDisable(requestOfStudent.get() == -1);
        } else {
            miTutorRequestCancel.setDisable(requestOfStudent.get() == -1);
            miTutorAnswerLoad.setDisable(requestOfStudent.get() == -1);
        }
    }


    //__________________________________________________________________________________________________________________
    //    Actions of students
    //------------------------------------------------------------------------------------------------------------------

    @FXML
    public void onTutorRequestSend(ActionEvent actionEvent) throws RemoteException {
        if (Student.sendRequest()) {
            miTutorRequestSend.setDisable(true);
            Alerts.showInfo("Request received by tutor", "You can have only one request at all time.");
        } else {
            Alerts.showInfo("Oops this should not happen", "It seems there is already a request from you.");
        }
    }

    @FXML
    public void onTutorRequestCancel(ActionEvent actionEvent) throws RemoteException {
        if (Student.cancel()) {
            miTutorRequestSend.setDisable(false);
            Alerts.showInfo("Request canceled", "Your request as been canceled, this also has removed your tutors answer if available.");
        } else {
            Alerts.showInfo("Request seems to be checkout", "You can cancel your request anytime, but not when your tutor is working on it.");
        }
    }

    @FXML
    public void onTutorAnswerLoad(ActionEvent actionEvent) throws RemoteException {
        if (Student.loadAnswer()) {
            miTutorRequestSend.setDisable(false);
            Alerts.showInfo("Answer received from tutor", "Your program and world has been updated according to your tutors answer.");
        } else {
            Alerts.showInfo("No answer yet", "Please be patient and try again later.");
        }
    }


    //__________________________________________________________________________________________________________________
    //    Actions of tutors
    //------------------------------------------------------------------------------------------------------------------

    @FXML
    public void onTutorRequestLoad(ActionEvent actionEvent) throws RemoteException {
        // get alls request and map to student ids
        List<String> keys = Tutor.listRequests().stream().map(String::valueOf).collect(Collectors.toList());

        // special cases
        if (keys.isEmpty()) {
            Alerts.showInfo("No requests yet", "Thats good, not?");
            return;
        }
        if (keys.size() == 1) {
            loadRequest(keys.get(0));
            return;
        }

        // ask which student request is to load
        Alerts.requestDecision(keys,
                "Which student request should be loaded as a new tab?",
                "Load Request of Student: ")
                .ifPresent(this::loadRequest);
    }

    private void loadRequest(String studentId) {
        try {
            // load request
            int student = Integer.valueOf(studentId);
            Model request = Tutor.loadRequest(student);

            // add new tab for request
            Tab tab = tabsController.addNew("Request_Student_" + student, request);

            // cancel request answer on tab close
            tab.setOnCloseRequest(event -> {
                Alerts.confirmClose(event,
                        "Please confirm the CLOSE action.",
                        "Closing this program tab (Request_Student_" + student + ") puts the student request back.\n\nStill continue?");
                // abort close action if needed
                if (event.isConsumed()) return;

                // close permitted: cancel request answer
                try {
                    Tutor.cancel(student, request);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } finally {
                    tabsController.close(tab);
                }

            });
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onTutorAnswerCancel(ActionEvent actionEvent) throws RemoteException {
        if (Tutor.cancel(ModelCtx.get().requestOfStudent.get(), ModelCtx.get())){
            tabsController.closeActive();
            Alerts.showInfo("Answer canceled", "Students request has been put back and is available again.");
        } else {
            Alerts.showInfo("Oops this should not happen", "I could not put back the students request.");
        }
    }

    @FXML
    public void onTutorAnswerSend(ActionEvent actionEvent) throws RemoteException {
        if (Tutor.sendAnswer()) {
            tabsController.closeActive();
            Alerts.showInfo("Answer sended", "Your answer is now available to load by your student.");
        } else {
            Alerts.showInfo("Oops this should not happen", "I could not send your answer.");
        }
    }
}

