package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.controller.util.Alerts;
import de.nowakhub.miniwelt.model.interfaces.Observer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class Observable {

    private boolean silence = false;
    private final transient Map<String, Observer> observers = new HashMap<>();


    public void notifyObservers() {
        if (silence) return;
        for (Observer observer : observers.values()) {
            observer.update();
        }
    }

    public void addObserver(String key, Observer observer) {
        //if (!observers.containsKey(key))
        observers.put(key, observer);
    }

    public void deleteObserver(String key) {
        observers.remove(key);
    }

    public void silently(Callable callable) {
        Exception exception = null;
        try {
            silence = true;
            callable.call();
        } catch (Exception ex) {
            exception = ex;
        } finally {
            silence = false;
            notifyObservers();
            if (exception != null) Alerts.showException(exception);
        }
    }

}
