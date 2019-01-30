package de.nowakhub.miniwelt.controller.util;

import de.nowakhub.miniwelt.model.World;
import de.nowakhub.miniwelt.view.Editor;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.util.Pair;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;

public class Student {

    private static Server server;

    // used to enable menu tutor
    private static ReadOnlyBooleanWrapper connected = new ReadOnlyBooleanWrapper(false);
    public static boolean isConnected() {
        return connected.get();
    }
    public static ReadOnlyBooleanWrapper connectedProperty() {
        return connected;
    }

    // used to update stage title
    private static ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper(-1);
    public static Integer getId() {
        return id.get();
    }
    public static ReadOnlyIntegerProperty idProperty() {
        return id;
    }

    /**
     * Connects to tutor server (assumes that {@link PropsCtx#hasServer()} is true)
     * on failure it makes three retries every 5 minute
     * @see PropsCtx
     */
    public static void connect() {
        // dont block javafx application thread on retrying
        Thread thread = new Thread(() -> {
            try {
                int retries = 0;
                do {
                    try {
                        // conntect to server
                        Registry registry = LocateRegistry.getRegistry(PropsCtx.getPort());
                        Student.server = (Server) registry.lookup("server");
                        connected.set(true);

                        // get own student id
                        Integer nextStudentId = Student.server.nextStudentId();
                        Platform.runLater(() -> id.set(nextStudentId));
                    } catch (Exception ex) {
                        TimeUnit.MINUTES.sleep(5);
                        retries++;
                    }
                } while (0 < retries && retries < 4);
            } catch (Exception ex) {
                Alerts.showException(ex);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * makes an request for the tutor (sends complete model to the server)
     * @see Server
     */
    public static boolean sendRequest() throws RemoteException {
        // send request
        boolean sended = server.sendRequest(id.get(), ModelCtx.program(), ModelCtx.world());

        // mark model as already send
        if (sended) ModelCtx.get().requestOfStudent.set(id.get());

        return sended;
    }

    /**
     * asks server if there is an answer and only load his program and world
     * assumes that the current model is the marked one by {@link #sendRequest()}
     * @see Server
     */
    public static boolean loadAnswer() throws RemoteException {
        Pair<String, World> answer = server.loadAnswer(id.get());
        if (answer != null) {
            // override current model
            ModelCtx.setWorld(answer.getValue());
            ModelCtx.setProgram(answer.getKey());
            ModelCtx.get().programState.set(Editor.STATE.DIRTY);

            // unmark model
            ModelCtx.get().requestOfStudent.set(-1);
            return true;
        }
        return false;
    }

    /**
     * cancel student request on server if not already loaded by tutor
     * @see Server
     */
    public static boolean cancel() throws RemoteException {
        // cancel request
        boolean canceled = server.cancelRequest(id.get());

        // unmark model
        if (canceled) ModelCtx.get().requestOfStudent.set(-1);

        return canceled;
    }
}
