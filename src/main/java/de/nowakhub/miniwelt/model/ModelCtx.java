package de.nowakhub.miniwelt.model;

public final class ModelCtx {

    private static Observable observable= new Observable() {};

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

    public static void setWorld(World world) {
        ModelCtx.model.setWorld(world);
    }

    public static Actor actor() {
        return model.getActor();
    }

    public static void setActor(Actor actor) {
        ModelCtx.model.setActor(actor);
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
