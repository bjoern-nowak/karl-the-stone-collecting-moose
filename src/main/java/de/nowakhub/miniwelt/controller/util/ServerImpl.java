package de.nowakhub.miniwelt.controller.util;

import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.model.World;
import javafx.util.Pair;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * a tutor server (RMI) implementation
 * @see Server
 */
public class ServerImpl extends UnicastRemoteObject implements Server {

    private int students;
    private Map<Integer, Pair<String, World>> requests = new HashMap<>();
    private Map<Integer, Pair<String, World>> answers = new HashMap<>();

    public ServerImpl() throws RemoteException {
        students = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer nextStudentId() throws RemoteException {
        return students++;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendRequest(Integer student, String program, World world) throws RemoteException {
        Pair<String, World> previous = requests.putIfAbsent(student, new Pair<>(program, world));
        return previous == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean cancelRequest(Integer student) throws RemoteException {
        Pair<String, World> removedReq = requests.remove(student);
        Pair<String, World> removedAns = answers.remove(student);
        return removedReq != null || removedAns != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model loadRequest(Integer student) throws RemoteException {
        Pair<String, World> request = requests.get(student);
        if (request == null) return null;
        requests.remove(student);

        Model model = new Model(request.getKey());
        model.setWorld(request.getValue());
        model.requestOfStudent.set(student);

        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Integer> listRequests() throws RemoteException {
        return requests.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendAnswer(Integer student, String program, World world) throws RemoteException {
        Pair<String, World> previous = answers.putIfAbsent(student, new Pair<>(program, world));
        return previous == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<String, World> loadAnswer(Integer student) throws RemoteException {
        Pair<String, World> answer = answers.get(student);
        if (answer == null) return null;
        answers.remove(student);

        return answer;
    }
}
