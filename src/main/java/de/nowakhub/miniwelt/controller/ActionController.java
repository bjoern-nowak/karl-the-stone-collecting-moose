package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.Field;
import de.nowakhub.miniwelt.model.ModelCtx;
import de.nowakhub.miniwelt.model.World;
import de.nowakhub.miniwelt.model.exceptions.InvalidInputException;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.canvas.Canvas;
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
import java.util.Arrays;
import java.util.Collection;


public class ActionController {

    private final String INVISIBLE = "import de.nowakhub.miniwelt.model.Invisible;";
    private final String PREFIX = INVISIBLE + " public class %s extends " + Actor.class.getName() + " { public";
    private final String PREFIX_REGEX = INVISIBLE + " public class \\w+ extends " + Actor.class.getName() + " { public";
    private final String POSTFIX = "}";

    private FileChooser programChooser;
    private FileChooser worldChooser;
    private FileChooser exportChooser;

    private Simulation simulation;


    private TabsController tabsController; // TODO move up into ModelCtx?

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
            sliderSimSpeed.valueProperty().addListener((obs, oldV, newV) -> ModelCtx.get().statusText.setValue("Speed changed to " + newV.intValue()));
        }

        initFileChoosers();
    }

    private void initFileChoosers() {
        // make sure folder exists
        File dir = Paths.get("programs").toFile();
        if (!dir.exists()) dir.mkdirs();

        programChooser = new FileChooser();
        programChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java", "*.java"));
        programChooser.setInitialDirectory(dir);

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

    void postInitialize(TabsController tabsController) {
        this.tabsController = tabsController;
        ModelCtx.get().simulationRunning.addListener((obs, oldV, newV) -> {
            btnSimStart.setDisable(newV);
            btnSimPause.setDisable(!newV);
            btnSimStop.setDisable(!newV);
        });
        ModelCtx.get().simulationRunning.set(false);
    }


    private Window getWindow() {
        return sliderSimSpeed.getScene().getWindow();
    }


    // TODO get file handle logic out of tabsController
    @FXML
    public void onProgramNew(ActionEvent actionEvent) {
        tabsController.addNew();
        compile(true);
    }
    @FXML
    public void onProgramOpen(ActionEvent actionEvent) {
        File file = programChooser.showOpenDialog(getWindow());
        if (file != null) {
            tabsController.open(file);
            compile(true);
        }
    }
    @FXML
    public void onProgramSave(ActionEvent actionEvent) {
        saveProgram(false);
    }
    @FXML
    public void onProgramSaveAs(ActionEvent actionEvent) {
        saveProgram(true);
    }
    private boolean saveProgram(boolean forceUserInput) {
        File file = ModelCtx.get().programFile;
        if (file == null || forceUserInput) {
            if (file != null) {
                //programChooser.setInitialDirectory(ModelCtx.get().programFile.getParentFile());
                programChooser.setInitialFileName(ModelCtx.get().programFile.getName());
            }
            file = programChooser.showSaveDialog(getWindow());
            programChooser.setInitialFileName("");
        }

        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file);
                 PrintWriter printWriter = new PrintWriter(fileWriter)) {
                printWriter.println(String.format(PREFIX, file.getName().replace(".java", "")));
                printWriter.println(ModelCtx.get().program.get());
                printWriter.print(POSTFIX);
            } catch (IOException ex) {
                Alerts.showException(ex);
                return false;
            }

            tabsController.saved(ModelCtx.get().programFile, file);
            ModelCtx.get().programFile = file;
            ModelCtx.get().programDirty.set(false);
            ModelCtx.get().programCompiled.set(false);
            ModelCtx.get().programSave = ModelCtx.get().program.get();
            return true;
        }
        return false;
    }
    @FXML
    public void onProgramPrint(ActionEvent actionEvent) {
        PrinterJob job = PrinterJob.createPrinterJob();
        Platform.runLater(() -> {
            if (job != null)
                if (job.showPrintDialog(getWindow())) {
                    Text title = new Text(TabsController.getTabText(ModelCtx.get().programFile));
                    title.setFont(Font.font("Consolas", 14));
                    Text program = new Text(ModelCtx.get().program.get());
                    program.setFont(Font.font("Consolas", 8));
                    VBox box = new VBox(title, program);
                    if (job.printPage(box))
                        job.endJob();
                }

        });
    }
    @FXML
    public void onProgramCompile(ActionEvent actionEvent) {
        boolean saved = saveProgram(false);
        if (saved) {
            compile(false);
        }
    }

    boolean compile(boolean silently) {
        if (ModelCtx.get().programFile == null) return false;

        File classDir = Paths.get("out/production/miniwelt_bjnowak").toAbsolutePath().toFile();
        File sourceDir = Paths.get("src/main/java").toAbsolutePath().toFile();

        String[] args = new String[] {
                "-classpath", System.getProperty("java.class.path") + ";" + classDir.toString(),
                "-sourcepath", sourceDir.toString(),
                "-d", ModelCtx.get().programFile.getParent(),
                ModelCtx.get().programFile.toString()
        };
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        boolean success = javac.run(null, null, errStream, args) == 0;
        if (!success) {
            ModelCtx.get().programCompiled.set(false);
            if (!silently) Alerts.showError(
                    "Compiler says no. He complains:",
                    errStream.toString());
        } else {
            ModelCtx.get().programCompiled.set(true);
            if (!silently) Alerts.showInfo(
                    "Compiler says yes.",
                    "Compile was successful.");

            try {
                URL[] urls = new URL[] { ModelCtx.get().programFile.getParentFile().toURI().toURL() };
                ClassLoader cl = new URLClassLoader(urls);
                String clsName = ModelCtx.get().programFile.getName().substring(0, ModelCtx.get().programFile.getName().length() - 5);
                Class<?> cls = cl.loadClass(clsName);
                ModelCtx.setActor((Actor) cls.newInstance());
                ModelCtx.actor().setInteraction(ModelCtx.world());
                return true;
            } catch (Exception ex) {
                if (!silently) Alerts.showException(ex);
            }

        }
        return false;
    }

    @FXML
    public void onProgramExit(ActionEvent actionEvent) {
        TabsController.confirmExitIfNecessaery(actionEvent, tabsController.getTabs());
        if (!actionEvent.isConsumed()) Platform.exit();
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
            try (FileInputStream stream = new FileInputStream(file); ObjectInputStream ser = new ObjectInputStream(stream); XMLDecoder xml = new XMLDecoder(stream)) {
                if (file.getName().toLowerCase().endsWith(".ser")) ModelCtx.setWorld((World) ser.readObject());
                else if (file.getName().toLowerCase().endsWith(".xml")) ModelCtx.setWorld((World) xml.readObject());
            } catch (Exception ex) {
                Alerts.showException(ex);
            }
        }
    }
    @FXML
    public void onWorldSave(ActionEvent actionEvent) {
        File file = worldChooser.showSaveDialog(getWindow());
        if (file != null) {
            try (FileOutputStream stream = new FileOutputStream(file); ObjectOutputStream ser = new ObjectOutputStream(stream); XMLEncoder xml = new XMLEncoder(stream)) {
                if (file.getName().toLowerCase().endsWith(".ser")) ser.writeObject(ModelCtx.world());
                else if (file.getName().toLowerCase().endsWith(".xml")) xml.writeObject(ModelCtx.world());
            } catch (Exception ex) {
                Alerts.showException(ex);
            }
        }
    }
    @FXML
    public void onWorldExport(ActionEvent actionEvent) throws IOException {
        File file = exportChooser.showSaveDialog(getWindow());
        if (file != null) {
            Canvas worldCanvas = ModelCtx.get().worldCanvas;
            WritableImage worldSnapshot = worldCanvas.snapshot(null, new WritableImage((int) worldCanvas.getWidth(), (int) worldCanvas.getHeight()));
            String format = file.getName().toLowerCase().substring(file.getName().length() - 3);
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
                "Change dimension of the model.world (Rows x Cols).\nRequire format: [nxn | n e IN]",
                "Please enter dimension:"
        ).ifPresent(input -> {
            String[] dimension = input.split("x");
            if (!input.matches("\\d+x\\d+") || dimension.length != 2) throw new InvalidInputException("Invalid format for model.world dimension");
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




    @FXML
    public void onActorBagChangeSize(ActionEvent actionEvent) {
        Alerts.requestInput(
                "" + ModelCtx.world().getActorBagMax(),
                "Change maximal size of actor bag.\nRequire format: [n | n e IN]",
                "Please enter maximal item count:"
        ).ifPresent(input -> {
            if (!input.matches("\\d+")) throw new InvalidInputException("Invalid format for maximal bag size");
            ModelCtx.world().setActorBagMax(Integer.valueOf(input));
        });
    }
    @FXML
    public void onActorBagChangeContent(ActionEvent actionEvent) {
        Alerts.requestInput(
                "" + ModelCtx.world().getActorBag(),
                "Change item count of actor bag.\nRequire format: [n | n e IN, n <= maximal bag size]",
                "Please enter item count smaller then " + (ModelCtx.world().getActorBagMax()+1) + ":"
        ).ifPresent(input -> {
            if (!input.matches("\\d+") || ModelCtx.world().getActorBagMax() < Integer.valueOf(input))
                throw new InvalidInputException("Invalid format for bag item count");
            ModelCtx.world().setActorBag(Integer.valueOf(input));
        });
    }
    @FXML
    public void onActorStepAhead(ActionEvent actionEvent) {
        ModelCtx.actor().stepAhead();
    }
    @FXML
    public void onActorTurnRight(ActionEvent actionEvent) {
        ModelCtx.actor().turnRight();
    }
    @FXML
    public void onActorBackToStart(ActionEvent actionEvent) {
        ModelCtx.actor().backToStart();
    }
    @FXML
    public void onActorAheadClear(ActionEvent actionEvent) {
        Alerts.showInfo(
                "Actor test command result",
                "Result of aheadClear(): " + ModelCtx.actor().aheadClear());
    }
    @FXML
    public void onActorBagEmpty(ActionEvent actionEvent) {
        Alerts.showInfo(
                "Actor test command result",
                "Result of bagEmpty(): " + ModelCtx.actor().bagEmpty());
    }
    @FXML
    public void onActorFoundItem(ActionEvent actionEvent) {
        Alerts.showInfo(
                "Actor test command result",
                "Result of foundItem(): " + ModelCtx.actor().foundItem());
    }
    @FXML
    public void onActorPickUp(ActionEvent actionEvent) {
        ModelCtx.actor().pickUp();
    }
    @FXML
    public void onActorDropDown(ActionEvent actionEvent) {
        ModelCtx.actor().dropDown();
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



    @FXML
    public void onExampleSave(ActionEvent actionEvent) {
        Alerts.requestDoubleInput(
                "Save example (program and world)",
                "Split Tags with comma, like: a,b,c",
                "Name:",
                "Tags:"
        ).ifPresent(input -> ExamplesDB.save(input.getKey(), Arrays.asList(input.getValue().trim().split(",")), ModelCtx.get()));

    }
    @FXML
    public void onExampleLoad(ActionEvent actionEvent) {
        Alerts.requestInput("",
                "Please enter a tag to search for.",
                "Tag:"
        ).ifPresent(input -> {
            Collection<String> examples = ExamplesDB.filter(input.trim());
            Alerts.requestDecision(examples,
                    "Load example (program and world)",
                    "Which example to load:"
            ).ifPresent(decision -> tabsController.addNew(decision, ExamplesDB.load(decision)));
        });
    }

}

