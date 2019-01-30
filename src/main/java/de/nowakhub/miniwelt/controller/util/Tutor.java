package de.nowakhub.miniwelt.controller.util;

import de.nowakhub.miniwelt.model.Model;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Tutor {

    private static Server server;
    private static Registry registry;;

    private static ReadOnlyBooleanWrapper started = new ReadOnlyBooleanWrapper(false);
    public static boolean getStarted() {
        return started.get();
    }
    public static ReadOnlyBooleanWrapper startedProperty() {
        return started;
    }

    /**
     * starts a tutor server (assumes that {@link PropsCtx#hasServer()} and {@link PropsCtx#isTutor()} is true)
     * on failure it makes three retries every 5 minute
     * @see PropsCtx
     */
    public static void start() {
        Thread thread = new Thread(() -> {
            try {
                int retries = 0;
                do {
                    try {
                        server = new ServerImpl();
                        registry = LocateRegistry.createRegistry(PropsCtx.getPort());
                        registry.rebind("server", server);
                        started.set(true);
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
     * @return true, only if a server is successful registered
     */
    public static boolean isStarted() {
        return registry != null;
    }

    /**
     * stops a tutor server if one is up
     */
    public static void stop() {
        try {
            if (registry != null) {
                UnicastRemoteObject.unexportObject(registry, true);
                UnicastRemoteObject.unexportObject(server, true);
            }
        } catch (Exception ex) {
            Alerts.showException(ex);
        }
    }


    /**
     * retrieve all requests from server
     * @see Server
     */
    public static Set<Integer> listRequests() throws RemoteException {
        return server.listRequests();
    }

    /**
     * asks server if there is an request of a specific student and load its complete model
     * @see Server
     */
    public static Model loadRequest(int student) throws RemoteException {
        return server.loadRequest(student);
    }

    /**
     * makes an answer available for an student request
     * @see Server
     */
    public static boolean sendAnswer() throws RemoteException {
        return server.sendAnswer(ModelCtx.get().requestOfStudent.get(), ModelCtx.program(), ModelCtx.world());
    }

    /**
     * cancel answer and puts back request to the server
     * @see Server
     */
    public static boolean cancel(int student, Model request) throws RemoteException {
        return server.sendRequest(student, request.program.get(), request.getWorld());
    }
}
