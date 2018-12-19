package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.model.ModelCtx;
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


public class TabsController {
    private List<File> openFiles = new ArrayList<>();

    @FXML
    private TabPane tabPane;

    public void initialize() {
        // set model context on tab switch
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            ModelCtx.set(getModel(newV));
        });
        addTab(null, null);
    }


    void addNew() {
        addTab(null, null);
    }

    void saved(File oldFile, File newFile) {
        openFiles.remove(oldFile);
        openFiles.add(newFile);
        getTab(ModelCtx.get()).setText(getTabText(newFile));
    }

    void open(File file) {
        if (openFiles.contains(file)) {
            tabPane.getTabs().stream()
                    .filter(tab -> file.equals(getModel(tab).programFile))
                    .findFirst()
                    .ifPresent(tab -> tabPane.getSelectionModel().select(tab));
        } else {
            try {
                // java7 feature: used for reading small files
                List<String> lines = Files.readAllLines(file.toPath());
                Tab newTab = addTab(file, String.join("\n", lines.subList(1, lines.size() - 1)));
                openFiles.add(file);
                if (newTab != null) getModel(newTab).programDirty.set(false);
            } catch (IOException ex) {
                Alerts.showException(ex);
            }
        }
    }

    void close(Tab tab) {
        openFiles.remove(getModel(tab).programFile);
        tabPane.getTabs().remove(tab);
    }

    private Tab addTab(File file, String fileContent) {
        try {
            Model model = new Model(file, fileContent);
            TabController tabController = new TabController();

            FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/de/nowakhub/miniwelt/view/tab.fxml"));
            // TODO reuse world/program controller (one controller of each for hole application)
            tabLoader.setController(tabController);

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

            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
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
        if (getModel(event).programDirty.get()) {
            Alerts.confirmClose(event,
                    "Please confirm the CLOSE action.",
                    "This program tab (" + getTabText(ModelCtx.get().programFile) + ") is changed and unsaved.\n\nStill continue?");
        }
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
        }
    }


    ObservableList<Tab> getTabs() {
        return tabPane.getTabs();
    }

    private Tab getTab(Model model) {
        return getTabs().filtered(tab -> getModel(tab).equals(model)).get(0);
    }

    private static Collection<Tab> getDirtyTabs(Collection<Tab> tabs) {
        return tabs.stream()
                .filter(tab -> getModel(tab).programDirty.get())
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
