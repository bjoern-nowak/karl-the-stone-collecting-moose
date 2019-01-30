package de.nowakhub.miniwelt.controller.util;

import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.model.World;
import de.nowakhub.miniwelt.model.util.Observable;

import java.util.concurrent.Callable;

public class ModelCtx {

    // need to hold a instance as static (cannot extend)
    private static Observable observable = new Observable() {};

    private static Model model;


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

    public static Actor actor() {
        return model.getActor();
    }

    public static String program() {
        return model.program.get();
    }


    //__________________________________________________________________________________________________________________
    //    getter/setter
    //------------------------------------------------------------------------------------------------------------------

    public static Model getModel() {
        return model;
    }

    public static void setModel(Model model) {
        ModelCtx.model = model;
        observable.notifyObservers();
    }

    public static World getWorld() {
        return model.getWorld();
    }

    public static void setWorld(World world) {
        model.setWorld(world);
    }

    public static Actor getActor() {
        return model.getActor();
    }

    public static void setActor(Actor actor) {
        model.setActor(actor);
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

    /**
     * @see Observable#notifyObservers()
     */
    public static void notifyObservers() {
        observable.notifyObservers();
    }

    /**
     * @see Observable#addObserver(String, Runnable)
     */
    public static void addObserver(String key, Runnable observer) {
        observable.addObserver(key, observer);
    }

    /**
     * @see Observable#deleteObserver(String)
     */
    public static void deleteObserver(String key) {
        observable.deleteObserver(key);
    }

    /**
     * @see Observable#silently(Callable)
     */
    public static void silently(Callable callable) {
        observable.silently(callable);
    }
}
