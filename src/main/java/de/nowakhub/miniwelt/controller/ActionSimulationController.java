package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.ModelCtx;
import de.nowakhub.miniwelt.controller.util.Simulation;
import de.nowakhub.miniwelt.model.Model;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;


public abstract class ActionSimulationController extends ActionActorController {

    private ChangeListener<Boolean> btnSimRunningListener;
    private ChangeListener<Number> sliderSimListener;


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

    @FXML
    public Slider sliderSimSpeed;


    public ActionSimulationController() {
        // alternate menu items and buttons depending on simulation state
        btnSimRunningListener = (obs, oldV, newV) -> {
            btnSimStart.setDisable(newV);
            btnSimPause.setDisable(!newV);
            btnSimStop.setDisable(!newV);

            miSimStart.setDisable(newV);
            miSimPause.setDisable(!newV);
            miSimStop.setDisable(!newV);
        };

        // calculate simulation speed on slider change
        sliderSimListener = (observable, oldValue, newValue) -> {
            // TODO you can do better math instead of ternary operation
            ModelCtx.get().simulationSpeed = newValue.intValue() <= 0.0 ? 10 : 10 * newValue.intValue();
            RootController.statusText.set("Speed set to: " + ModelCtx.get().simulationSpeed + "ms which are " + (ModelCtx.get().simulationSpeed / 1000.0) + "s");
        };
    }

    public void initialize() {
        super.initialize();

        // configure speed slider (not in fxml because its business logic and not layout)
        sliderSimSpeed.setMin(0);
        sliderSimSpeed.setMax(300);
        sliderSimSpeed.setMajorTickUnit(50);
        sliderSimSpeed.setMinorTickCount(50);
        sliderSimSpeed.setShowTickMarks(true);
        sliderSimSpeed.setSnapToTicks(true);

        // listen to tab switches and update view
        ModelCtx.addObserver("actionSimulation", this::update);
        update();
        sliderSimSpeed.valueProperty().setValue(50);
    }

    /**
     * sets listener and initially fires them
     */
    private void update() {
        // listen on simulation state and init value
        ModelCtx.get().simulationRunning.removeListener(btnSimRunningListener);
        ModelCtx.get().simulationRunning.addListener(btnSimRunningListener);
        btnSimRunningListener.changed(null, null, ModelCtx.get().simulationRunning.get());

        // listen on slider updates and init value
        sliderSimSpeed.valueProperty().removeListener(sliderSimListener);
        sliderSimSpeed.valueProperty().addListener(sliderSimListener);
        sliderSimListener.changed(null, null, sliderSimSpeed.getValue());
    }


    @FXML
    public void onSimStartOrContinue(ActionEvent actionEvent) {
        Model model = ModelCtx.get();

        // compile if necessary
        if (model.isProgramDirty() || !model.isProgramCompiled()) onProgramCompile(actionEvent);

        // start or continue simulation
        if (model.isProgramCompiled()) {

            // only run if actor and start are on field
            model.getWorld().existActor();
            model.getWorld().existsStart();

            model.simulationRunning.set(true);
            if (model.simulation == null || !model.simulation.isAlive()) {
                model.simulation = new Simulation(model);
                model.simulation.start();
            } else {
                model.simulation.proceed();
            }
        }
    }

    @FXML
    public void onSimPause(ActionEvent actionEvent) {
        ModelCtx.get().simulationRunning.set(false);
        ModelCtx.get().simulation.pause();
    }

    @FXML
    public void onSimStop(ActionEvent actionEvent) {
        ModelCtx.get().simulationRunning.set(false);
        ModelCtx.get().simulation.interrupt();
    }

}

