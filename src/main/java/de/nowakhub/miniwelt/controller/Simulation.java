package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.model.exceptions.PublicException;
import javafx.application.Platform;

public class Simulation extends Thread {

    private final Model model;
    private volatile boolean paused = false;

    public Simulation(Model model) {
        this.model = model;
        this.setDaemon(true);
    }

    public void run() {
        try {
            // TODO everything is bad about this, also to many "start" attemps break the world canvas or throws exceptions
            synchronized (this) {
                while(paused) wait();
                model.getActor().main();
            }
        } catch (InterruptedException ex) {
            // someone really want us to stop
        } catch (PublicException ex) {
            Platform.runLater(() -> {
                Alerts.playWarning();
                Alerts.showException(ex);
            });
        } finally {
            model.simulationRunning.set(false);
        }
    }

    public void pause() {
        paused = true;
    }

    synchronized public void proceed() {
        paused = false;
        notify();
    }

    public void terminate() {
        interrupt();
    }
}
