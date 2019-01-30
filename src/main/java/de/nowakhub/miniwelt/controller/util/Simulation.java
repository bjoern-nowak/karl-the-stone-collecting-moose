package de.nowakhub.miniwelt.controller.util;

import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.model.World;
import javafx.application.Platform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Simulation extends Thread {

    private final Model model;
    private boolean paused = false;

    private byte[] startWorld;

    /**
     * @param model containing program and world to run
     */
    public Simulation(Model model) {
        this.model = model;
        this.setDaemon(true);
    }

    /**
     * saves world prior run to reload on {@link #interrupt()} and executes {@link Actor#main()} of given model
     */
    @Override
    public synchronized void run() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            // pre-run: save world
            oos.writeObject(ModelCtx.world());
            startWorld = baos.toByteArray();

            // run simulation
            model.getActor().main();
        } catch (Exception ex) {
            Platform.runLater(() -> {
                Alerts.showException(ex);
            });
        } finally {
            model.simulationRunning.set(false);
        }
    }

    public void pause() {
        paused = true;
    }

    public synchronized void proceed() {
        paused = false;
        notify();
    }

    /**
     * stops thread and load pre-run world
     */
    @Override
    public void interrupt() {
        super.interrupt();

        // after-run by stop: reload world
        try (ByteArrayInputStream bais = new ByteArrayInputStream(startWorld);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            model.setWorld((World) ois.readObject());
        } catch (Exception ex) {
            Platform.runLater(() -> {
                Alerts.showException(ex);
            });
        }
    }

    /**
     * sets thread to sleep for a defined duration and then wait if pause flag is set.
     */
    public boolean delay() {
        try {
            sleep(model.simulationSpeed);
            while(paused) wait();
            return true;
        } catch (InterruptedException ex) {
            super.interrupt();
            return false;
        }
    }
}
