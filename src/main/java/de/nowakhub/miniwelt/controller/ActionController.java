package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.Field;
import de.nowakhub.miniwelt.model.World;
import de.nowakhub.miniwelt.model.exceptions.InvalidInputException;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;


public class ActionController extends ModelController {

    private Simulation simulation;

    private FileChooser fileChooser;


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
            sliderSimSpeed.valueProperty().addListener((obs, oldV, newV) -> model.statusText.setValue("Speed changed to " + newV.intValue()));
        }


        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Serialized", "*.ser"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
        File dir = Paths.get("programs").toFile();
        if (!dir.exists()) dir.mkdirs();
        fileChooser.setInitialDirectory(dir);
    }

    void postInitialize() {
        model.simulationRunning.addListener((obs, oldV, newV) -> {
            btnSimStart.setDisable(newV);
            btnSimPause.setDisable(!newV);
            btnSimStop.setDisable(!newV);
        });
        model.simulationRunning.set(false);
    }


    private Window getWindow() {
        return sliderSimSpeed.getScene().getWindow();
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
    public void onProgramSaveAs(ActionEvent actionEvent) {
        model.tabsController.save(model, true);
    }
    @FXML
    public void onProgramPrint(ActionEvent actionEvent) {
        PrinterJob job = PrinterJob.createPrinterJob();
        Platform.runLater(() -> {
            if (job != null)
                if (job.showPrintDialog(getWindow())) {
                    Text title = new Text(TabsController.getTabText(model.programFile));
                    title.setFont(Font.font("Consolas", 14));
                    Text program = new Text(model.program.get());
                    program.setFont(Font.font("Consolas", 8));
                    VBox box = new VBox(title, program);
                    if (job.printPage(box))
                        job.endJob();
                }

        });
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
                model.setActor((Actor) cls.newInstance());
                model.getActor().setInteraction(model.getWorld());
            } catch (Exception ex) {
                if (!silently) Alerts.showException(ex);
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
        model.getWorld().reset();
    }
    @FXML
    public void onWorldRandom(ActionEvent actionEvent) {
        model.getWorld().random();
    }
    @FXML
    public void onWorldLoad(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(getWindow());
        if (file != null) {
            try (FileInputStream stream = new FileInputStream(file); ObjectInputStream ser = new ObjectInputStream(stream); XMLDecoder xml = new XMLDecoder(stream)) {
                if (file.getName().toLowerCase().endsWith(".ser")) model.setWorld((World) ser.readObject());
                else if (file.getName().toLowerCase().endsWith(".xml")) model.setWorld((World) xml.readObject());
            } catch (Exception ex) {
                Alerts.showException(ex);
            }
        }
    }
    @FXML
    public void onWorldSave(ActionEvent actionEvent) {
        File file = fileChooser.showSaveDialog(getWindow());
        if (file != null) {
            try (FileOutputStream stream = new FileOutputStream(file); ObjectOutputStream ser = new ObjectOutputStream(stream); XMLEncoder xml = new XMLEncoder(stream)) {
                if (file.getName().toLowerCase().endsWith(".ser")) ser.writeObject(model.getWorld());
                else if (file.getName().toLowerCase().endsWith(".xml")) xml.writeObject(model.getWorld());
            } catch (Exception ex) {
                Alerts.showException(ex);
            }
        }
    }
    @FXML
    public void onWorldExport(ActionEvent actionEvent) throws IOException {

        // TODO optimize fileChooser creation (multiple places over controller does the same)
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GIF", "*.gif"));
        File dir = Paths.get("programs").toFile();
        if (!dir.exists()) dir.mkdirs();
        fileChooser.setInitialDirectory(dir);

        File file = fileChooser.showSaveDialog(getWindow());
        if (file != null) {
            WritableImage image = new WritableImage((int) model.frame.getWidth(), (int) model.frame.getHeight());
            model.frame.snapshot(null, image);
            if (file.getName().toLowerCase().endsWith(".png")) ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            else if (file.getName().toLowerCase().endsWith(".jpg")) ImageIO.write(SwingFXUtils.fromFXImage(image, null), "jpg", file);
            else if (file.getName().toLowerCase().endsWith(".gif")) ImageIO.write(SwingFXUtils.fromFXImage(image, null), "gif", file);
        }
    }
    @FXML
    public void onWorldPrint(ActionEvent actionEvent) {
        PrinterJob job = PrinterJob.createPrinterJob();
        Platform.runLater(() -> {
            if (job != null)
                if (job.showPrintDialog(getWindow()))
                    if (job.printPage(model.frame))
                        job.endJob();
        });
    }

    @FXML
    public void onWorldChangeSize(ActionEvent actionEvent) {
        Alerts.requestInput(
                actionEvent,
                model.getWorld().getSizeRow() + "x" + model.getWorld().getSizeCol(),
                "Change dimension of the model.world (Rows x Cols).\nRequire format: [nxn | n e IN]",
                "Please enter dimension:"
        ).ifPresent(input -> {
            String[] dimension = input.split("x");
            if (!input.matches("\\d+x\\d+") || dimension.length != 2) throw new InvalidInputException("Invalid format for model.world dimension");
            model.getWorld().resize(Integer.valueOf(dimension[0]), Integer.valueOf(dimension[1]));
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
                "" + model.getWorld().getActorBagMax(),
                "Change maximal size of actor bag.\nRequire format: [n | n e IN]",
                "Please enter maximal item count:"
        ).ifPresent(input -> {
            if (!input.matches("\\d+")) throw new InvalidInputException("Invalid format for maximal bag size");
            model.getWorld().setActorBagMax(Integer.valueOf(input));
        });
    }
    @FXML
    public void onActorBagChangeContent(ActionEvent actionEvent) {
        Alerts.requestInput(
                actionEvent,
                "" + model.getWorld().getActorBag(),
                "Change item count of actor bag.\nRequire format: [n | n e IN, n <= maximal bag size]",
                "Please enter item count smaller then " + (model.getWorld().getActorBagMax()+1) + ":"
        ).ifPresent(input -> {
            if (!input.matches("\\d+") || model.getWorld().getActorBagMax() < Integer.valueOf(input))
                throw new InvalidInputException("Invalid format for bag item count");
            model.getWorld().setActorBag(Integer.valueOf(input));
        });
    }
    @FXML
    public void onActorStepAhead(ActionEvent actionEvent) {
        model.getActor().stepAhead();
    }
    @FXML
    public void onActorTurnRight(ActionEvent actionEvent) {
        model.getActor().turnRight();
    }
    @FXML
    public void onActorBackToStart(ActionEvent actionEvent) {
        model.getActor().backToStart();
    }
    @FXML
    public void onActorAheadClear(ActionEvent actionEvent) {
        Alerts.showInfo(
                "Actor test command result",
                "Result of aheadClear(): " + model.getActor().aheadClear());
    }
    @FXML
    public void onActorBagEmpty(ActionEvent actionEvent) {
        Alerts.showInfo(
                "Actor test command result",
                "Result of bagEmpty(): " + model.getActor().bagEmpty());
    }
    @FXML
    public void onActorFoundItem(ActionEvent actionEvent) {
        Alerts.showInfo(
                "Actor test command result",
                "Result of foundItem(): " + model.getActor().foundItem());
    }
    @FXML
    public void onActorPickUp(ActionEvent actionEvent) {
        model.getActor().pickUp();
    }
    @FXML
    public void onActorDropDown(ActionEvent actionEvent) {
        model.getActor().dropDown();
    }




    @FXML
    public void onSimStartOrContinue(ActionEvent actionEvent) {
        model.simulationRunning.set(true);
        if (simulation == null || !simulation.isAlive()) {
            simulation = new Simulation(model);
            simulation.start();
        } else {
            simulation.proceed();
        }
    }
    @FXML
    public void onSimPause(ActionEvent actionEvent) {
        model.simulationRunning.set(false);
        simulation.pause();
    }
    @FXML
    public void onSimStop(ActionEvent actionEvent) {
        model.simulationRunning.set(false);
        simulation.terminate();
    }
}

