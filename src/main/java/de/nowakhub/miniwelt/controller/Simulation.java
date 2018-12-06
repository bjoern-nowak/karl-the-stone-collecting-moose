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
            synchronized (this) {
                while(paused) wait();
                model.world.getActor().main();
            }
        } catch (InterruptedException ex) {
            // someone really want us to stop
        } catch (PublicException ex) {
            Platform.runLater(() -> {
                Alerts.playWarning();
                Alerts.showException(null, ex);
            });
        } finally {
            model.simulationRunning.set(false);
        }
    }

    public void pause() {
        model.simulationRunning.set(false);
        paused = true;
    }

    synchronized public void proceed() {
        model.simulationRunning.set(true);
        paused = false;
        notify();
    }

    public void terminate() {
        model.simulationRunning.set(false);
        interrupt();
    }
}
