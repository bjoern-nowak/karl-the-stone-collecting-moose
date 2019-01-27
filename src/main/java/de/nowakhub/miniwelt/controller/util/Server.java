package de.nowakhub.miniwelt.controller.util;

import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.model.World;
import javafx.util.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * a tutor server (RMI)
 * @see ServerImpl
 */
public interface Server extends Remote {

    /**
     * next unique id in session
     */
    Integer nextStudentId() throws RemoteException;

    /**
     * send student request to tutor server
     */
    boolean sendRequest(Integer student, String program, World world) throws RemoteException;

    /**
     * removes student request if not already checkout by tutor (removes answer if available)
     */
    boolean cancelRequest(Integer student) throws RemoteException;

    /**
     * load specific student request
     */
    Model loadRequest(Integer student) throws RemoteException;

    /**
     * list all available student requests by their student id
     */
    Set<Integer> listRequests() throws RemoteException;

    /**
     * send an answer for a student request
     */
    boolean sendAnswer(Integer student, String program, World world) throws RemoteException;

    /**
     * load student request answer if available
     */
    Pair<String, World> loadAnswer(Integer student) throws RemoteException;
}
