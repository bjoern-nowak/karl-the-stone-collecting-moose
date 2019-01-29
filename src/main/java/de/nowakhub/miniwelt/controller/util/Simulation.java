package de.nowakhub.miniwelt.controller.util;

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

    public Simulation(Model model) {
        this.model = model;
        this.setDaemon(true);
    }

    public synchronized void run() {
        while (!interrupted()) {
            try {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    // pre-run: save world
                    oos.writeObject(ModelCtx.world());
                    startWorld = baos.toByteArray();

                    // run simulation
                    model.getActor().main();
                }
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    Alerts.showException(ex);
                });
            }
        }
    }

    public void pause() {
        paused = true;
    }

    public synchronized void proceed() {
        paused = false;
        notify();
    }

    public void terminate() {
        interrupt();

        // after-run: load world
        try (ByteArrayInputStream bais = new ByteArrayInputStream(startWorld);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            model.setWorld((World) ois.readObject());
        } catch (Exception ex) {
            Platform.runLater(() -> {
                Alerts.showException(ex);
            });
        }
    }

    public void delay() {
        try {
            sleep(model.simulationSpeed);
            while(paused) wait();
        } catch (InterruptedException ex) {
            // someone really want us to stop
            interrupt();
        }
    }
}
