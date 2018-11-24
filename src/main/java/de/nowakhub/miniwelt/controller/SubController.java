package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;

public abstract class SubController {

    Model model;

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
