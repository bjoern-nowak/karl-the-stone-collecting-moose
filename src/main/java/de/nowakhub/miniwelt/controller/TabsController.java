package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.Alerts;
import de.nowakhub.miniwelt.controller.util.Message;
import de.nowakhub.miniwelt.controller.util.ModelCtx;
import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.view.Editor;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for tab handle logic
 */
public class TabsController {
    private List<File> openFiles = new ArrayList<>();

    @FXML
    private TabPane tabPane;

    public void initialize() {
        // set model context on tab switch
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> ModelCtx.set(getModel(newValue)));

        // add default tab
        addTab();
    }


    /**
     * add a default tab with a default model
     */
    Tab addNew() {
        return addTab();
    }

    /**
     * add a new tab with a specific model and name that tab
     */
    Tab addNew(String programName, Model model) {
        Tab tab = addTab(null, model);
        if (tab != null) tab.setText(programName);
        return tab;
    }

    /**
     * update opened files and tab text (file may been moved or renamed)
     */
    void saved(File oldFile, File newFile) {
        openFiles.remove(oldFile);
        openFiles.add(newFile);
        getTab(ModelCtx.get()).setText(getTabText(newFile));
    }

    /**
     * add a new tab by loading a file
     */
    void open(File file) {
        if (openFiles.contains(file)) {
            // file already opened, find and focus that tab
            tabPane.getTabs().stream()
                    .filter(tab -> file.equals(getModel(tab).programFile))
                    .findFirst()
                    .ifPresent(tab -> tabPane.getSelectionModel().select(tab));
        } else {
            try {
                // java7 feature: used for reading small files
                List<String> lines = Files.readAllLines(file.toPath());

                // add new tab
                Tab newTab = addTab(file, String.join("\n", lines.subList(1, lines.size() - 1)));

                // on success update states
                if (newTab != null) {
                    getModel(newTab).programState.set(Editor.STATE.SAVED);
                    openFiles.add(file);
                }
            } catch (IOException ex) {
                Alerts.showException(ex);
            }
        }
    }

    void close(Tab tab) {
        openFiles.remove(getModel(tab).programFile);
        tabPane.getTabs().remove(tab);
    }

    void closeActive() {
        close(getActiveTab());
    }

    private Tab addTab() {
        return addTab(null, new Model());
    }

    private Tab addTab(File file, String program) {
        return addTab(file, new Model(file, program));
    }

    private Tab addTab(File file, Model model) {
        try {
            // every tab has its own instance
            TabController tabController = new TabController();
            FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/de/nowakhub/miniwelt/view/tab.fxml"), Message.getBundle());
            tabLoader.setController(tabController);

            // configure tab
            Tab tab = tabLoader.load();
            tab.setUserData(model);
            tab.setText(getTabText(file));
            tab.setOnCloseRequest(event -> {
                confirmCloseIfNecessaery(event);
                if (!event.isConsumed()) openFiles.remove(getModel(event).programFile);
            });
            tab.setOnClosed(event -> {
                if (tabPane.getTabs().isEmpty()) Platform.exit();
            });

            // add and select new tab
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            // pass model through
            tabController.postInitialize(model);

            return tab;
        } catch (IOException ex) {
            Alerts.showException(ex);
        }
        return null;
    }

    /**
     * Confirmation is required if target program tab is dirty (changed and unsaved)
     * Event gets consumed if user canceled action
     */
    private static void confirmCloseIfNecessaery(Event event) {
        if (getModel(event).isProgramDirty()) {
            Alerts.confirmClose(event,
                    "Please confirm the CLOSE action.",
                    "This program tab (" + getTabText(ModelCtx.get().programFile) + ") is changed and unsaved.\n\nStill continue?");
        };
    }

    /**
     * Confirmation is required if any program tab is dirty (changed and unsaved)
     * Event gets consumed if user canceled action
     */
    public static void confirmExitIfNecessaery(Event event, Collection<Tab> tabs) {
        Collection<Tab> dirtyTabs = getDirtyTabs(tabs);
        if (!dirtyTabs.isEmpty()) {
            StringBuilder dirtyTabNames = new StringBuilder();
            for (Tab tab : dirtyTabs) {
                if (dirtyTabNames.length() > 0) dirtyTabNames.append(", ");
                dirtyTabNames.append(tab.getText());
            }
            Alerts.confirmClose(event,
                    "Please confirm the EXIT action.",
                    "Following program tabs are new or changed and unsaved:\n" + dirtyTabNames +"\n\nStill continue?");
        };
    }


    ObservableList<Tab> getTabs() {
        return tabPane.getTabs();
    }

    Tab getActiveTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    private Tab getTab(Model model) {
        return getTabs().filtered(tab -> getModel(tab).equals(model)).get(0);
    }

    private static Collection<Tab> getDirtyTabs(Collection<Tab> tabs) {
        return tabs.stream()
                .filter(tab -> getModel(tab).isProgramDirty())
                .collect(Collectors.toList());
    }

    private static Model getModel(Tab tab) {
        return ((Model) tab.getUserData());
    }
    private static Model getModel(Event event) {
        return getModel((Tab) event.getTarget());
    }

    static String getTabText(File file) {
        return file != null ? file.getName().replace(".java", "") : "DefaultProgram";
    }
}
