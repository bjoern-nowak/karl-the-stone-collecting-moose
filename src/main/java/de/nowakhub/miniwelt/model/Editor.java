package de.nowakhub.miniwelt.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.*;
import javafx.scene.control.TextArea;

public class Editor extends TextArea {

    private BooleanProperty dirty = buildBooleanProperty(PseudoClass.getPseudoClass("dirty"), "dirty");
    private BooleanProperty compiled = buildBooleanProperty(PseudoClass.getPseudoClass("compiled"), "compiled");

    private BooleanPropertyBase buildBooleanProperty(PseudoClass pseudoClass, String name) {
        return new BooleanPropertyBase(false) {
            @Override
            protected void invalidated() {
                pseudoClassStateChanged(pseudoClass, get());
            }

            @Override
            public Object getBean() {
                return Editor.this;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    public boolean isDirty() {
        return dirty.get();
    }

    public BooleanProperty dirtyProperty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty.set(dirty);
    }

    public boolean isCompiled() {
        return compiled.get();
    }

    public BooleanProperty compiledProperty() {
        return compiled;
    }

    public void setCompiled(boolean compiled) {
        this.compiled.set(compiled);
    }
}
