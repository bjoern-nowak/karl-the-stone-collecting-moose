package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
                    // java7 feature: used for reading small files
                    List<String> lines = Files.readAllLines(file.toPath());
                    addTab(file, String.join("\n", lines.subList(1, lines.size() - 1)));
                } catch (IOException e) {
                    e.printStackTrace(); // TODO crap
                }
                // alternative to above:
                /*
                try (FileReader fileReader = new FileReader(file);
                     BufferedReader reader = new BufferedReader(fileReader)) {
                    StringBuilder builder = new StringBuilder();
                    reader.lines().skip(1).forEachOrdered(builder::append);
                    // TODO  remove last line
                    addTab(file, builder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
            }
        });
    }

    void save(Model model) {
        Platform.runLater(() -> {
            if (model.programFile != null) {
                //fileChooser.setInitialDirectory(model.programFile.getParentFile());
                fileChooser.setInitialFileName(model.programFile.getName());
            }
            File file = fileChooser.showSaveDialog(tabPane.getScene().getWindow());
            if (file != null) {
                try (FileWriter fileWriter = new FileWriter(file);
                     PrintWriter printWriter = new PrintWriter(fileWriter)) {
                    printWriter.println(String.format(PREFIX, file.getName().replace(".java", "")));
                    printWriter.println(model.program.get());
                    printWriter.print(POSTFIX);

                    model.programFile = file;
                    model.programDirty = false;
                    model.programSave = model.program.get();
                    model.tabText.set(file.getName());
                } catch (IOException e) {
                    e.printStackTrace(); // TODO crap
                }
            }
            fileChooser.setInitialFileName("");
        });
    }

    void close(Tab tab) {
        tabPane.getTabs().remove(tab);
    }

    private void addTab(File file, String fileContent) {
        try {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/de/nowakhub/miniwelt/view/root.fxml"));
            Model model = new Model(this, file, fileContent);
            tabLoader.setController(new RootController(model));
            Tab tab = tabLoader.load();
            tab.setUserData(model);
            tab.setText(file != null ? file.getName() : "DefaultProgram");
            tab.setOnCloseRequest(event -> confirmCloseIfNecessaery(event, model));
            tab.setOnClosed(event -> {
                if (tabPane.getTabs().isEmpty()) Platform.exit();
            });
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        } catch (IOException ex) {
            ex.printStackTrace(); // TODO crap
        }
    }

    public static void confirmCloseIfNecessaery(Event event, Model model) {
        if (model.programDirty) {
            confirmClose(event,
                    "Please confirm the CLOSE action.",
                    "This program tab (" + model.tabText.get() + ") is changed and unsaved.\nStill continue?");
        }
    }

    public static void confirmExitIfNecessaery(Event event, Collection<Tab> tabs) {
        Collection<Tab> dirtyTabs = getDirtyTabs(tabs);
        if (!dirtyTabs.isEmpty()) {
            String dirtyTabNames = "";
            for (Tab tab : dirtyTabs) {
                if (dirtyTabNames.length() > 0) dirtyTabNames += ", ";
                dirtyTabNames += tab.getText();
            }
            confirmClose(event,
                    "Please confirm the EXIT action.",
                    "Following program tabs are changed and unsaved:\n" + dirtyTabNames +"\nStill continue?");
        }
    }

    private static Collection<Tab> getDirtyTabs(Collection<Tab> tabs) {
        return tabs.stream()
                .filter(tab -> ((Model) tab.getUserData()).programDirty)
                .collect(Collectors.toList());
    }

    private static void confirmClose(Event event, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait()
                .filter(buttonType -> buttonType.equals(ButtonType.CANCEL))
                .ifPresent(buttonType -> event.consume());
        // TODO ask to save all files
    }

    public ObservableList<Tab> getTabs() {
        return tabPane.getTabs();
    }
}
