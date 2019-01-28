package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.Alerts;
import de.nowakhub.miniwelt.controller.util.ModelCtx;
import de.nowakhub.miniwelt.model.Actor;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;


public abstract class ActionProgramController extends ActionBaseController {

    private final String INVISIBLE = "import de.nowakhub.miniwelt.model.util.Invisible;";
    private final String PREFIX = INVISIBLE + " public class %s extends " + Actor.class.getName() + " { public";
    private final String PREFIX_REGEX = INVISIBLE + " public class \\w+ extends " + Actor.class.getName() + " { public";
    private final String POSTFIX = "}";

    private FileChooser programChooser;

    public void initialize() {
        super.initialize();
        initFileChoosers();
    }


    private void initFileChoosers() {
        // make sure folder exists
        File dir = Paths.get("programs").toFile();
        if (!dir.exists()) dir.mkdirs();

        programChooser = new FileChooser();
        programChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java", "*.java"));
        programChooser.setInitialDirectory(dir);
    }


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
            programChooser.setInitialFileName(tabsController.getActiveTab().getText());
        }

        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file);
                 PrintWriter printWriter = new PrintWriter(fileWriter)) {
                printWriter.println(String.format(PREFIX, file.getName().replace(".java", "")));
                printWriter.println(ModelCtx.program());
                printWriter.print(POSTFIX);
            } catch (IOException ex) {
                Alerts.showException(ex);
                return false;
            }

            tabsController.saved(ModelCtx.get().programFile, file);
            ModelCtx.get().programFile = file;
            ModelCtx.get().programDirty.set(false);
            ModelCtx.get().programCompiled.set(false);
            ModelCtx.get().programSave = ModelCtx.program();
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
                    Text program = new Text(ModelCtx.program());
                    program.setFont(Font.font("Consolas", 8));
                    VBox box = new VBox(title, program);
                    if (job.printPage(box))
                        job.endJob();
                }

        });
    }
    @FXML
    public void onProgramCompile(ActionEvent actionEvent) {
        // TODO compile to temp if not saved (dont force saving for compiling)
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
                ModelCtx.actor().setInteractable(ModelCtx.world());
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

}

