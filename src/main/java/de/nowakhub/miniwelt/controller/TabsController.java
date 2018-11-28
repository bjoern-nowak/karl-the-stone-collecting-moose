package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.Model;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// TODO or use different scenes, tab selecting changes visible scene (which has the same fxml structure)
public class TabsController {

    private final String PREFIX = " public class %s extends " + Actor.class.getName() + " { public";
    private final String PREFIX_REGEX = "public class \\w+ extends " + Actor.class.getName() + " { public";
    private final String POSTFIX = "}";

    private FileChooser fileChooser;
    private List<File> openFiles = new ArrayList<>();

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
        File file = fileChooser.showOpenDialog(tabPane.getScene().getWindow());
        if (file != null) {

            if (openFiles.contains(file)) {
                tabPane.getTabs().stream()
                        .filter(tab -> file.equals(getModel(tab).programFile))
                        .findFirst()
                        .ifPresent(tab -> tabPane.getSelectionModel().select(tab));
                return;
            }

            try {
                // java7 feature: used for reading small files
                List<String> lines = Files.readAllLines(file.toPath());
                Tab newTab = addTab(file, String.join("\n", lines.subList(1, lines.size() - 1)));
                openFiles.add(file);
                if (newTab != null) getModel(newTab).programDirty.set(false);
            } catch (IOException ex) {
                Alerts.showException(null, ex);
            }
        }
    }

    boolean save(Model model, boolean forceUserInput) {


        File file = model.programFile;
        if (file == null || forceUserInput) {
            if (file != null) {
                //fileChooser.setInitialDirectory(model.programFile.getParentFile());
                fileChooser.setInitialFileName(model.programFile.getName());
            }
            file = fileChooser.showSaveDialog(tabPane.getScene().getWindow());
            fileChooser.setInitialFileName("");
        }

        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file);
                 PrintWriter printWriter = new PrintWriter(fileWriter)) {
                printWriter.println(String.format(PREFIX, file.getName().replace(".java", "")));
                printWriter.println(model.program.get());
                printWriter.print(POSTFIX);

                openFiles.remove(model.programFile);
                openFiles.add(file);
                model.programFile = file;
                model.programDirty.set(false);
                model.programSave = model.program.get();
                getTab(model).setText(getTabText(file));
                return true;
            } catch (IOException ex) {
                Alerts.showException(null, ex);
                return false;
            }
        }
        return false;
    }

    void close(Tab tab) {
        openFiles.remove(getModel(tab).programFile);
        tabPane.getTabs().remove(tab);
    }

    private Tab addTab(File file, String fileContent) {
        try {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/de/nowakhub/miniwelt/view/root.fxml"));
            Model model = new Model(this, file, fileContent);
            RootController rootController = new RootController(model);
            tabLoader.setController(rootController);
            Tab tab = tabLoader.load();
            tab.setUserData(model);
            tab.setText(getTabText(file));
            tab.setOnCloseRequest(event -> {
                confirmCloseIfNecessaery(event, model);
                if (!event.isConsumed()) openFiles.remove(model.programFile);
            });
            tab.setOnClosed(event -> {
                if (tabPane.getTabs().isEmpty()) Platform.exit();
            });
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            rootController.compileSilently();
            return tab;
        } catch (IOException ex) {
            Alerts.showException(null, ex);
        }
        return null;
    }

    /**
     * Confirmation is required if target program tab is dirty (changed and unsaved)
     * Event gets consumed if user canceled action
     */
    private static void confirmCloseIfNecessaery(Event event, Model model) {
        if (model.programDirty.get()) {
            Alerts.confirmClose(event,
                    "Please confirm the CLOSE action.",
                    "This program tab (" + getTabText(model.programFile) + ") is changed and unsaved.\n\nStill continue?");
        }
    }

    /**
     * Confirmation is required if any program tab is dirty (changed and unsaved)
     * Event gets consumed if user canceled action
     */
    public static void confirmExitIfNecessaery(Event event, Collection<Tab> tabs) {
        Collection<Tab> dirtyTabs = getDirtyTabs(tabs);
        if (!dirtyTabs.isEmpty()) {
            String dirtyTabNames = "";
            for (Tab tab : dirtyTabs) {
                if (dirtyTabNames.length() > 0) dirtyTabNames += ", ";
                dirtyTabNames += tab.getText();
            }
            Alerts.confirmClose(event,
                    "Please confirm the EXIT action.",
                    "Following program tabs are changed and unsaved:\n" + dirtyTabNames +"\n\nStill continue?");
        }
    }


    ObservableList<Tab> getTabs() {
        return tabPane.getTabs();
    }

    private static Collection<Tab> getDirtyTabs(Collection<Tab> tabs) {
        return tabs.stream()
                .filter(tab -> getModel(tab).programDirty.get())
                .collect(Collectors.toList());
    }

    private Tab getTab(Model model) {
        return getTabs().filtered(tab -> getModel(tab).equals(model)).get(0);
    }

    private static Model getModel(Tab tab) {
        return ((Model) tab.getUserData());
    }

    private static String getTabText(File file) {
        return file != null ? file.getName().replace(".java", "") : "DefaultProgram";
    }
}
