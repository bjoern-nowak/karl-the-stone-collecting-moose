package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.ModelCtx;
import de.nowakhub.miniwelt.controller.util.Simulation;
import de.nowakhub.miniwelt.model.interfaces.Observer;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;


public abstract class ActionSimulationController extends ActionActorController implements Observer {

    private Simulation simulation;
    private ChangeListener<Boolean> btnSimListener;


    @FXML
    public MenuItem miSimStart;
    @FXML
    public MenuItem miSimPause;
    @FXML
    public MenuItem miSimStop;

    @FXML
    public Button btnSimStart;
    @FXML
    public Button btnSimPause;
    @FXML
    public Button btnSimStop;


    public ActionSimulationController() {
        btnSimListener = (obs, oldV, newV) -> {
            btnSimStart.setDisable(newV);
            btnSimPause.setDisable(!newV);
            btnSimStop.setDisable(!newV);

            miSimStart.setDisable(newV);
            miSimPause.setDisable(!newV);
            miSimStop.setDisable(!newV);
        };

    }

    public void initialize() {
        super.initialize();
        ModelCtx.addObserver("actionSimulation", this);
        update();
    }

    @Override
    public void update() {
        ModelCtx.get().simulationRunning.removeListener(btnSimListener);
        ModelCtx.get().simulationRunning.addListener(btnSimListener);
        btnSimListener.changed(null, null, ModelCtx.get().simulationRunning.get());
    }


    @FXML
    public void onSimStartOrContinue(ActionEvent actionEvent) {
        boolean dirty = ModelCtx.get().programDirty.get();
        boolean compiled = ModelCtx.get().programCompiled.get();
        if ((dirty) || !compiled) onProgramCompile(actionEvent);

        if (ModelCtx.get().programCompiled.get()) {
            ModelCtx.get().simulationRunning.set(true);
            if (simulation == null || !simulation.isAlive()) {
                simulation = new Simulation(ModelCtx.get());
                simulation.start();
            } else {
                simulation.proceed();
            }
        }
    }

    @FXML
    public void onSimPause(ActionEvent actionEvent) {
        ModelCtx.get().simulationRunning.set(false);
        simulation.pause();
    }

    @FXML
    public void onSimStop(ActionEvent actionEvent) {
        ModelCtx.get().simulationRunning.set(false);
        simulation.terminate();
    }

}

