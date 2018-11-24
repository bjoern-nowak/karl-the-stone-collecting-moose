package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// TODO or use different scenes, tab selecting changes visible scene (which has the same fxml structure)
public class TabsController {

    private final String CLASSNAME_PREFIX = "UserActorImpl_";
    private final String PREFIX = "public class " + CLASSNAME_PREFIX + "%s extends Actor { public";
    private final String PREFIX_REGEX = "public class \\w+ extends Actor { public";
    private final String POSTFIX = "}";

    private FileChooser fileChooser;

    @FXML
    private TabPane tabPane;

    public TabsController() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java", "*.java"));
        File dir = Paths.get("programs").toFile();
        dir.mkdirs();
        if (dir.exists()) fileChooser.setInitialDirectory(dir);
    }

    public void initialize() {
        addTab(null, null);
    }

    void add() {
        addTab(null, null);
    }

    void open() {
        Platform.runLater(() -> {
            File file = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
            if (file != null) {

                try {
                    List<String> lines = Files.readAllLines(file.toPath());
                    addTab(file, String.join("\n", lines.subList(1, lines.size() - 1)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                try (FileReader fileReader = new FileReader(file);
//                     BufferedReader reader = new BufferedReader(fileReader)) {
//                    StringBuilder builder = new StringBuilder();
//                    reader.lines().skip(1).forEachOrdered(builder::append);
//                    // TODO remove last line
//                    addTab(file, builder.toString());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        });
    }

    void save(Model model) {
        Platform.runLater(() -> {
//            if (model.programFile != null) {
//                fileChooser.setInitialDirectory(model.programFile.getParentFile());
//                fileChooser.setInitialFileName(model.programFile.getName());
//            }
            File file = fileChooser.showSaveDialog(tabPane.getScene().getWindow());
            if (file != null) {
                try (FileWriter fileWriter = new FileWriter(file);
                     PrintWriter printWriter = new PrintWriter(fileWriter)) {
                    printWriter.println(String.format(PREFIX, file.getName().replace(".java", "")));
                    printWriter.println(model.program.get());
                    printWriter.print(POSTFIX);

                    model.programFile = file;
                    model.tabText.set(file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void close(Tab tab) {
        tabPane.getTabs().remove(tab);
    }

    private void addTab(File file, String fileContent) {
        try {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/de/nowakhub/miniwelt/view/root.fxml"));
            tabLoader.setController(new RootController(new Model(this, file, fileContent)));
            Tab tab = tabLoader.load();
            tab.setText(file != null ? file.getName() : "DefaultProgram");
            tab.setOnClosed(event -> {
                // TODO add check if program editor is dirty
                if (tabPane.getTabs().isEmpty()) Platform.exit();
            });
            tabPane.getTabs().add(tab);
        } catch (IOException ex) {
            ex.printStackTrace(); // TODO crap
        }
    }
}
