package de.nowakhub.miniwelt.controller.util;

import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.model.Observable;
import de.nowakhub.miniwelt.model.World;
import de.nowakhub.miniwelt.model.interfaces.Observer;

public class ModelCtx {

    private static Observable observable = new Observable() {};

    private static Model model;

    public static Model getModel() {
        return model;
    }

    public static void setModel(Model model) {
        ModelCtx.model = model;
        observable.notifyObservers();
    }

    //__________________________________________________________________________________________________________________
    //    convenient getter/setter
    //------------------------------------------------------------------------------------------------------------------

    public static Model get() {
        return model;
    }

    public static void set(Model model) {
        ModelCtx.model = model;
        observable.notifyObservers();
    }

    public static World world() {
        return model.getWorld();
    }

    public static World getWorld() {
        return model.getWorld();
    }

    public static void setWorld(World world) {
        model.setWorld(world);
    }

    public static Actor actor() {
        return model.getActor();
    }

    public static Actor getActor() {
        return model.getActor();
    }

    public static void setActor(Actor actor) {
        model.setActor(actor);
    }

    public static String program() {
        return model.program.get();
    }

    public static String getProgram() {
        return model.program.get();
    }

    public static void setProgram(String program) {
        model.program.set(program);
    }


    //__________________________________________________________________________________________________________________
    //    observable
    //------------------------------------------------------------------------------------------------------------------

    public static void notifyObservers() {
        observable.notifyObservers();
    }

    public static void addObserver(String key, Observer observer) {
        observable.addObserver(key, observer);
    }

    public static void deleteObserver(String key) {
        observable.deleteObserver(key);
    }

}
