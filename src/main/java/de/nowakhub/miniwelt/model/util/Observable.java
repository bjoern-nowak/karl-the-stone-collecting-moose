package de.nowakhub.miniwelt.model.util;

import de.nowakhub.miniwelt.controller.util.Alerts;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class Observable {

    protected boolean silence = false;
    protected final transient Map<String, Runnable> observers = new HashMap<>();


    /**
     * call all observers; if not in silence mode
     */
    public void notifyObservers() {
        if (silence) return;
        for (Runnable observer : observers.values()) {
            observer.run();
        }
    }

    /**
     * adds a observer by key; override if already present like {@link Map#put}
     * @param key under which the observer will be added
     */
    public void addObserver(String key, Runnable observer) {
        //if (!observers.containsKey(key))
        observers.put(key, observer);
    }

    /**
     * remove observer by key
     * @param key under which the observer was added
     */
    public void deleteObserver(String key) {
        observers.remove(key);
    }

    /**
     * execute the callable without notifying observers during execution, only afterwards or on errors
     * @param callable a task which may calls {@link #notifyObservers()}, which will get suppressed
     */
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
