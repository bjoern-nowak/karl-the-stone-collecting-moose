package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;

public abstract class ModelController {

    Model model;

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        if (this.model == null) this.model = model;
    }
}
