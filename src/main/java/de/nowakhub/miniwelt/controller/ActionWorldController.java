package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.Alerts;
import de.nowakhub.miniwelt.controller.util.ModelCtx;
import de.nowakhub.miniwelt.model.World;
import de.nowakhub.miniwelt.model.util.Field;
import de.nowakhub.miniwelt.model.exceptions.InvalidInputException;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.nio.file.Paths;


/**
 * implements all actions possible through menu of world and toolbar buttons for world
 */
public abstract class ActionWorldController extends ActionProgramController {

    private FileChooser worldChooser;
    private FileChooser exportChooser;


    @FXML
    public RadioMenuItem miMouseModePlaceObstacle;
    @FXML
    public RadioMenuItem miMouseModePlaceItem;
    @FXML
    public RadioMenuItem miMouseModePlaceActor;
    @FXML
    public RadioMenuItem miMouseModePlaceStart;
    @FXML
    public RadioMenuItem miMouseModePlaceFree;

    @FXML
    public ToggleButton btnMouseModePlaceObstacle;
    @FXML
    public ToggleButton btnMouseModePlaceItem;
    @FXML
    public ToggleButton btnMouseModePlaceActor;
    @FXML
    public ToggleButton btnMouseModePlaceStart;
    @FXML
    public ToggleButton btnMouseModePlaceFree;


    public void initialize() {
        super.initialize();

        // bind corresponding menu items with toolbar buttons
        miMouseModePlaceObstacle.selectedProperty().bindBidirectional(btnMouseModePlaceObstacle.selectedProperty());
        miMouseModePlaceItem.selectedProperty().bindBidirectional(btnMouseModePlaceItem.selectedProperty());
        miMouseModePlaceActor.selectedProperty().bindBidirectional(btnMouseModePlaceActor.selectedProperty());
        miMouseModePlaceStart.selectedProperty().bindBidirectional(btnMouseModePlaceStart.selectedProperty());
        miMouseModePlaceFree.selectedProperty().bindBidirectional(btnMouseModePlaceFree.selectedProperty());

        initFileChoosers();
    }

    private void initFileChoosers() {
        // make sure folder exists
        File dir = Paths.get("worlds").toFile();
        if (!dir.exists()) dir.mkdirs();

        // configure file choosers
        worldChooser = new FileChooser();
        worldChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Serialized", "*.ser"));
        worldChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
        worldChooser.setInitialDirectory(dir);
        exportChooser = new FileChooser();
        exportChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        exportChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        exportChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GIF", "*.gif"));
        exportChooser.setInitialDirectory(dir);
    }


    @FXML
    public void onWorldReset(ActionEvent actionEvent) {
        ModelCtx.world().reset();
    }

    @FXML
    public void onWorldRandom(ActionEvent actionEvent) {
        ModelCtx.world().random();
    }

    @FXML
    public void onWorldLoad(ActionEvent actionEvent) {
        File file = worldChooser.showOpenDialog(getWindow());
        if (file != null) {
            try (FileInputStream stream = new FileInputStream(file);
                 ObjectInputStream ser = new ObjectInputStream(stream);
                 XMLDecoder xml = new XMLDecoder(stream)) {

                // check file type and load appropriate
                if (file.getName().toLowerCase().endsWith(".ser"))
                    ModelCtx.setWorld((World) ser.readObject());
                else if (file.getName().toLowerCase().endsWith(".xml"))
                    ModelCtx.setWorld((World) xml.readObject());

            } catch (Exception ex) {
                Alerts.showException(ex);
            }
        }
    }

    @FXML
    public void onWorldSave(ActionEvent actionEvent) {
        File file = worldChooser.showSaveDialog(getWindow());
        if (file != null) {
            try (FileOutputStream stream = new FileOutputStream(file);
                 ObjectOutputStream ser = new ObjectOutputStream(stream);
                 XMLEncoder xml = new XMLEncoder(stream)) {

                // check file type and save appropriate
                if (file.getName().toLowerCase().endsWith(".ser"))
                    ser.writeObject(ModelCtx.world());
                else if (file.getName().toLowerCase().endsWith(".xml"))
                    xml.writeObject(ModelCtx.world());

            } catch (Exception ex) {
                Alerts.showException(ex);
            }
        }
    }

    @FXML
    public void onWorldExport(ActionEvent actionEvent) throws IOException {
        File file = exportChooser.showSaveDialog(getWindow());
        if (file != null) {
            // get special object for this case (printable canvas instance)
            Canvas worldCanvas = ModelCtx.get().worldCanvas;

            // get picture and name it
            WritableImage worldSnapshot = worldCanvas.snapshot(null, new WritableImage((int) worldCanvas.getWidth(), (int) worldCanvas.getHeight()));
            String format = file.getName().toLowerCase().substring(file.getName().length() - 3);

            // print
            ImageIO.write(SwingFXUtils.fromFXImage(worldSnapshot, null), format, file);
        }
    }

    @FXML
    public void onWorldPrint(ActionEvent actionEvent) {
        PrinterJob job = PrinterJob.createPrinterJob();
        Platform.runLater(() -> {
            if (job != null)
                if (job.showPrintDialog(getWindow()))
                    if (job.printPage(ModelCtx.get().worldCanvas))
                        job.endJob();
        });
    }

    @FXML
    public void onWorldChangeSize(ActionEvent actionEvent) {
        Alerts.requestInput(
                ModelCtx.world().getSizeRow() + "x" + ModelCtx.world().getSizeCol(),
                "Change dimension of the world (Rows x Cols).\nRequire format: [nxn | n e IN]",
                "Please enter dimension:"
        ).ifPresent(input -> {
            String[] dimension = input.split("x");
            if (!input.matches("\\d+x\\d+") || dimension.length != 2) throw new InvalidInputException("Invalid format for world dimension");
            ModelCtx.world().resize(Integer.valueOf(dimension[0]), Integer.valueOf(dimension[1]));
        });
    }


    @FXML
    public void onMouseModePlaceObstacle(ActionEvent actionEvent) {
        ModelCtx.get().mouseMode.setValue(Field.OBSTACLE);
    }

    @FXML
    public void onMouseModePlaceActor(ActionEvent actionEvent) {
        ModelCtx.get().mouseMode.setValue(Field.ACTOR);
    }

    @FXML
    public void onMouseModePlaceItem(ActionEvent actionEvent) {
        ModelCtx.get().mouseMode.setValue(Field.ITEM);
    }

    @FXML
    public void onMouseModePlaceStart(ActionEvent actionEvent) {
        ModelCtx.get().mouseMode.setValue(Field.START);
    }

    @FXML
    public void onMouseModePlaceFree(ActionEvent actionEvent) {
        ModelCtx.get().mouseMode.setValue(Field.FREE);
    }

}

