package de.nowakhub.miniwelt.model;

import java.util.HashMap;
import java.util.Map;

public abstract class Observable {

    private final Map<String, Observer> observers = new HashMap<>();


    void notifyObservers() {
        for (Observer observer : observers.values()) {
            observer.update();
        }
    }

    public void addObserver(String key, Observer observer) {
        if (!observers.containsKey(key))
            observers.put(key, observer);
    }

    public void deleteObserver(String key) {
        observers.remove(key);
    }

}
