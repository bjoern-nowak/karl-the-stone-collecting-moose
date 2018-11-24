package de.nowakhub.miniwelt.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class TabsController {

    @FXML
    private TabPane tabPane;


    public void initialize() {
        addTab(null);
    }

    void add() {
        addTab(null);
    }

    void open() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) addTab(fileChooser.getSelectedFile());

    }

    void close(Tab tab) {
        tabPane.getTabs().remove(tab);
    }

    private void addTab(File program) {
        try {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/de/nowakhub/miniwelt/view/root.fxml"));
            tabLoader.setController(new RootController(this, program));
            Tab tab = tabLoader.load();
            tab.setText(program != null ? program.getName() : "DefaultProgram");
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
