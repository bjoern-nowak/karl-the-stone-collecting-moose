package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.Alerts;
import de.nowakhub.miniwelt.controller.util.ExamplesDB;
import de.nowakhub.miniwelt.controller.util.ModelCtx;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.Arrays;
import java.util.Collection;


public abstract class ActionExampleController extends ActionSimulationController {

    public void initialize() {
        super.initialize();
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

