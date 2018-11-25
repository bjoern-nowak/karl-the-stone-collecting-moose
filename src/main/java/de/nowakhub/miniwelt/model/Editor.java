package de.nowakhub.miniwelt.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.*;
import javafx.scene.control.TextArea;

public class Editor extends TextArea {

    private static final PseudoClass DIRTY_PSEUDO_CLASS = PseudoClass.getPseudoClass("dirty");

    private BooleanProperty dirty = new BooleanPropertyBase(false) {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(DIRTY_PSEUDO_CLASS, get());
        }

        @Override
        public Object getBean() {
            return Editor.this;
        }

        @Override
        public String getName() {
            return "dirty";
        }
    };

    public boolean isDirty() {
        return dirty.get();
    }

    public BooleanProperty dirtyProperty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty.set(dirty);
    }
}
