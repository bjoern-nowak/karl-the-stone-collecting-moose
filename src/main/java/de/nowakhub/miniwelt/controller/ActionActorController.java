package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.Alerts;
import de.nowakhub.miniwelt.controller.util.ModelCtx;
import de.nowakhub.miniwelt.model.exceptions.InvalidInputException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


public abstract class ActionActorController extends ActionWorldController {

    public void initialize() {
        super.initialize();
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
        ModelCtx.world().existActor();
        ModelCtx.actor().stepAhead();
    }

    @FXML
    public void onActorTurnRight(ActionEvent actionEvent) {
        ModelCtx.world().existActor();
        ModelCtx.actor().turnRight();
    }

    @FXML
    public void onActorBackToStart(ActionEvent actionEvent) {
        ModelCtx.world().existsStart();
        ModelCtx.actor().backToStart();
    }

    @FXML
    public void onActorAheadClear(ActionEvent actionEvent) {
        ModelCtx.world().existActor();
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
        ModelCtx.world().existActor();
        Alerts.showInfo(
                "Actor test command result",
                "Result of foundItem(): " + ModelCtx.actor().foundItem());
    }

    @FXML
    public void onActorPickUp(ActionEvent actionEvent) {
        ModelCtx.world().existActor();
        ModelCtx.actor().pickUp();
    }

    @FXML
    public void onActorDropDown(ActionEvent actionEvent) {
        ModelCtx.world().existActor();
        ModelCtx.actor().dropDown();
    }

}

