package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Field;
import de.nowakhub.miniwelt.model.exceptions.InvalidInputException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.nio.file.Paths;


public class ActionController extends ModelController {

    @FXML
    public ToggleGroup mouseModeToggleGroupMenubar;

    @FXML
    public ToggleGroup mouseModeToggleGroupToolbar;

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
        model.tabsController.save(model, false);
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        String actorPath = Paths.get("de/nowakhub/miniwelt/model/Actor.java").toAbsolutePath().toString();
        String userActorPath = model.programFile.toString();
        // TODO set classpath
        boolean success = javac.run(null, null, errStream, actorPath, userActorPath) == 0;
        if (!success) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Compiler says no. He complains:");
            alert.setContentText(errStream.toString());
            alert.show();
        } else {
            showNonBlockingInfo("Compiler says yes.", "Compile was successful.");
            // TODO add compiled user actor to world
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
        TextInputDialog dialog = new TextInputDialog(model.world.sizeRow() + "x" + model.world.sizeCol());
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Change dimension of the model.world (Rows x Cols).\nRequire format: [nxn | n e IN]");
        dialog.setContentText("Please enter dimension:");
        dialog.showAndWait().ifPresent(input -> {
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
        TextInputDialog dialog = new TextInputDialog("" + model.world.getActorBagMax());
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Change maximal size of actor bag.\nRequire format: [n | n e IN]");
        dialog.setContentText("Please enter maximal item count:");
        dialog.showAndWait().ifPresent(input -> {
            if (!input.matches("\\d+")) throw new InvalidInputException("Invalid format for maximal bag size");
            model.world.setActorBagMax(Integer.valueOf(input));
        });
    }
    @FXML
    public void onActorBagChangeContent(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("" + model.world.getActorBag());
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Change item count of actor bag.\nRequire format: [n | n e IN, n <= maximal bag size]");
        dialog.setContentText("Please enter item count smaller then " + (model.world.getActorBagMax()+1) + ":");
        dialog.showAndWait().ifPresent(input -> {
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
        showNonBlockingInfo(
                "Actor test command result",
                "Result of aheadClear(): " + model.world.aheadClear());
    }
    @FXML
    public void onActorBagEmpty(ActionEvent actionEvent) {
        showNonBlockingInfo(
                "Actor test command result",
                "Result of bagEmpty(): " + model.world.bagEmpty());
    }
    @FXML
    public void onActorFoundItem(ActionEvent actionEvent) {
        showNonBlockingInfo(
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
        model.statusText.setValue("onSimStartOrContinue");
    }
    @FXML
    public void onSimPause(ActionEvent actionEvent) {
        model.statusText.setValue("onSimPause");
    }
    @FXML
    public void onSimStop(ActionEvent actionEvent) {
        model.statusText.setValue("onSimStop");
    }


    public static void showNonBlockingInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }
}

