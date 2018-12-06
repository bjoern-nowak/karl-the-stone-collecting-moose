package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.Field;
import de.nowakhub.miniwelt.model.exceptions.InvalidInputException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;


public class ActionController extends ModelController {

    private Simulation simulation;



    @FXML
    public ToggleGroup mouseModeToggleGroupMenubar;

    @FXML
    public ToggleGroup mouseModeToggleGroupToolbar;


    @FXML
    public Button btnSimStart;
    @FXML
    public Button btnSimPause;
    @FXML
    public Button btnSimStop;
    @FXML
    public Slider sliderSimSpeed;


    public void initialize() {
        // TODO bind toggle groups

        if (sliderSimSpeed != null) {
            sliderSimSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
                model.statusText.setValue("Speed changed to " + newValue.intValue());
            });
        }
    }

    void postInitialize() {
        model.simulationRunning.addListener((observable, oldValue, newValue) -> {
            btnSimStart.setDisable(newValue);
            btnSimPause.setDisable(!newValue);
            btnSimStop.setDisable(!newValue);
        });
        model.simulationRunning.set(false);
    }


    @FXML
    public void onProgramNew(ActionEvent actionEvent) {
        model.tabsController.add();
    }
    @FXML
    public void onProgramOpen(ActionEvent actionEvent) {
        model.tabsController.open();
    }
    @FXML
    public void onProgramSave(ActionEvent actionEvent) {
        model.tabsController.save(model, false);
    }
    @FXML
    public void onProgramSaveUnder(ActionEvent actionEvent) {
        model.tabsController.save(model, true);
    }
    @FXML
    public void onProgramPrint(ActionEvent actionEvent) {
        model.statusText.setValue("onProgramPrint");
    }
    @FXML
    public void onProgramCompile(ActionEvent actionEvent) {
        boolean saved = model.tabsController.save(model, false);
        if (saved) {
            compile(false);
        }
    }

    void compile(boolean silently) {
        if (model.programFile == null) return;

        File classDir = Paths.get("out/production/miniwelt_bjnowak").toAbsolutePath().toFile();
        File sourceDir = Paths.get("src/main/java").toAbsolutePath().toFile();

        String[] args = new String[] {
                "-classpath", System.getProperty("java.class.path") + ";" + classDir.toString(),
                "-sourcepath", sourceDir.toString(),
                "-d", model.programFile.getParent(),
                model.programFile.toString()
        };
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        boolean success = javac.run(null, null, errStream, args) == 0;
        if (!success) {
            model.programCompiled.set(false);
            if (!silently) Alerts.showError(
                    "Compiler says no. He complains:",
                    errStream.toString());
        } else {
            model.programCompiled.set(true);
            if (!silently) Alerts.showInfo(
                    "Compiler says yes.",
                    "Compile was successful.");

            try {
                URL[] urls = new URL[] { model.programFile.getParentFile().toURI().toURL() };
                ClassLoader cl = new URLClassLoader(urls);
                String clsName = model.programFile.getName().substring(0, model.programFile.getName().length() - 5);
                Class<?> cls = cl.loadClass(clsName);
                model.world.setActor((Actor) cls.newInstance());
                model.world.getActor().setInteraction(model.world);
            } catch (Exception ex) {
                if (!silently) Alerts.showException(null, ex);
            }

        }
    }

    @FXML
    public void onProgramExit(ActionEvent actionEvent) {
        TabsController.confirmExitIfNecessaery(actionEvent, model.tabsController.getTabs());
        if (!actionEvent.isConsumed()) Platform.exit();
    }




    @FXML
    public void onWorldReset(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldReset");
    }
    @FXML
    public void onWorldLoad(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldLoad");
    }
    @FXML
    public void onWorldSave(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldSave");
    }
    @FXML
    public void onWorldExportPNG(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldExportPNG");
    }
    @FXML
    public void onWorldExportXML(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldExportXML");
    }
    @FXML
    public void onWorldExportText(ActionEvent actionEvent) {
        model.statusText.setValue("onWorldExportText");
    }
    @FXML
    public void onWorldChangeSize(ActionEvent actionEvent) {
        Alerts.requestInput(
                actionEvent,
                model.world.sizeRow() + "x" + model.world.sizeCol(),
                "Change dimension of the model.world (Rows x Cols).\nRequire format: [nxn | n e IN]",
                "Please enter dimension:"
        ).ifPresent(input -> {
            String[] dimension = input.split("x");
            if (!input.matches("\\d+x\\d+") || dimension.length != 2) throw new InvalidInputException("Invalid format for model.world dimension");
            model.world.resize(Integer.valueOf(dimension[0]), Integer.valueOf(dimension[1]));
        });
    }




    @FXML
    public void onMouseModePlaceObstacle(ActionEvent actionEvent) {
        model.mouseMode.setValue(Field.OBSTACLE);
    }
    @FXML
    public void onMouseModePlaceActor(ActionEvent actionEvent) {
        model.mouseMode.setValue(Field.ACTOR);
    }
    @FXML
    public void onMouseModePlaceItem(ActionEvent actionEvent) {
        model.mouseMode.setValue(Field.ITEM);
    }
    @FXML
    public void onMouseModePlaceStart(ActionEvent actionEvent) {
        model.mouseMode.setValue(Field.START);
    }
    @FXML
    public void onMouseModePlaceFree(ActionEvent actionEvent) {
        model.mouseMode.setValue(Field.FREE);
    }




    @FXML
    public void onActorBagChangeSize(ActionEvent actionEvent) {
        Alerts.requestInput(
                actionEvent,
                "" + model.world.getActorBagMax(),
                "Change maximal size of actor bag.\nRequire format: [n | n e IN]",
                "Please enter maximal item count:"
        ).ifPresent(input -> {
            if (!input.matches("\\d+")) throw new InvalidInputException("Invalid format for maximal bag size");
            model.world.setActorBagMax(Integer.valueOf(input));
        });
    }
    @FXML
    public void onActorBagChangeContent(ActionEvent actionEvent) {
        Alerts.requestInput(
                actionEvent,
                "" + model.world.getActorBag(),
                "Change item count of actor bag.\nRequire format: [n | n e IN, n <= maximal bag size]",
                "Please enter item count smaller then " + (model.world.getActorBagMax()+1) + ":"
        ).ifPresent(input -> {
            if (!input.matches("\\d+") || model.world.getActorBagMax() < Integer.valueOf(input))
                throw new InvalidInputException("Invalid format for bag item count");
            model.world.setActorBag(Integer.valueOf(input));
        });
    }
    @FXML
    public void onActorStepAhead(ActionEvent actionEvent) {
        model.world.stepAhead();
    }
    @FXML
    public void onActorTurnRight(ActionEvent actionEvent) {
        model.world.turnRight();
    }
    @FXML
    public void onActorBackToStart(ActionEvent actionEvent) {
        model.world.backToStart();
    }
    @FXML
    public void onActorAheadClear(ActionEvent actionEvent) {
        Alerts.showInfo(
                "Actor test command result",
                "Result of aheadClear(): " + model.world.aheadClear());
    }
    @FXML
    public void onActorBagEmpty(ActionEvent actionEvent) {
        Alerts.showInfo(
                "Actor test command result",
                "Result of bagEmpty(): " + model.world.bagEmpty());
    }
    @FXML
    public void onActorFoundItem(ActionEvent actionEvent) {
        Alerts.showInfo(
                "Actor test command result",
                "Result of foundItem(): " + model.world.foundItem());
    }
    @FXML
    public void onActorPickUp(ActionEvent actionEvent) {
        model.world.pickUp();
    }
    @FXML
    public void onActorDropDown(ActionEvent actionEvent) {
        model.world.dropDown();
    }




    @FXML
    public void onSimStartOrContinue(ActionEvent actionEvent) {
        if (simulation == null || !simulation.isAlive()) {
            simulation = new Simulation(model);
            simulation.start();
        } else {
            simulation.proceed();
        }
    }
    @FXML
    public void onSimPause(ActionEvent actionEvent) {
        simulation.pause();
    }
    @FXML
    public void onSimStop(ActionEvent actionEvent) {
        simulation.terminate();
    }
}

