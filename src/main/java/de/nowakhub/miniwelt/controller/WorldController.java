package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.Field;
import de.nowakhub.miniwelt.model.Invisible;
import de.nowakhub.miniwelt.model.Observer;
import de.nowakhub.miniwelt.model.exceptions.InternalUnkownFieldException;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

public class WorldController extends ModelController implements Observer {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private StackPane stackPane;

    @FXML
    private Canvas frame;
    private GraphicsContext gc;

    private final int TILE_SIZE = 32;
    private double zoom = 1.0;
    private Field dragging;
    private ContextMenu actorContextMenu;

    private Image obstacle = new Image("/images/world/debug/wall.png");
    private Image ground = new Image("/images/world/debug/ground.png");
    private Image item = new Image("/images/world/debug/item.png");
    private Image start = new Image("/images/world/debug/office.png");
    private Image actorU = new Image("/images/world/debug/karl_up.png");
    private Image actorL = new Image("/images/world/debug/karl_left.png");
    private Image actorD = new Image("/images/world/debug/karl_down.png");
    private Image actorR = new Image("/images/world/debug/karl_right.png");



    public void initialize() {
        gc = frame.getGraphicsContext2D();
        addEventHandler();
    }

    private void buildActorContextMenu() {
        actorContextMenu = new ContextMenu();
        actorContextMenu.setAutoFix(true);

        // for better user experience: add separators between specific method groups
        addMethodsToActorContextMenu(Arrays.asList("stepAhead", "turnRight", "pickUp", "dropDown"));
        addMethodsToActorContextMenu(Arrays.asList("aheadClear", "bagEmpty", "foundItem", "atStart"));
        Class<? extends Actor> cls = model.world.getActor().getClass();
        if (!cls.equals(Actor.class)) {
            Arrays.stream(cls.getDeclaredMethods()).forEach(this::addMethodToActorContextMenu);
        }
    }

    private void addMethodsToActorContextMenu(Collection<String> methods) {
        methods.forEach(method -> {
            try {
                Method m = model.world.getActor().getClass().getMethod(method);
                addMethodToActorContextMenu(m);
            } catch (NoSuchMethodException ex) {
                Alerts.showException(null, ex);
            }
        });
        actorContextMenu.getItems().add(new SeparatorMenuItem());
    }

    private void addMethodToActorContextMenu(Method method) {
        int modifiers = method.getModifiers();
        if (method.getAnnotation(Invisible.class) == null
                && !Modifier.isAbstract(modifiers)
                && !Modifier.isStatic(modifiers)
                && !Modifier.isPrivate(modifiers)) {
            MenuItem item = new MenuItem();
            if (method.getParameterCount() > 0) {
                String paramTypes = Arrays.stream(method.getParameterTypes())
                        .map(Class::getName)
                        .collect(Collectors.joining(","));
                item.setText(method.getReturnType().getName() + " " + method.getName() + "(" + paramTypes +")");
                item.setDisable(true);
            } else {
                item.setText(method.getReturnType().getName() + " " + method.getName() + "()");
                item.setOnAction(event -> {
                    try {
                        Object result = method.invoke(model.world.getActor());
                        if (result != null) { // instead of instanceOf check to enable various return types
                            Alerts.showInfo(method.getName() + "()", "" + result);
                        }
                    } catch (Exception ex) {
                        // TODO add sound
                        Alerts.showException(null, ex);
                    }
                });
            }
            actorContextMenu.getItems().add(item);
        }
    }

    private void addEventHandler() {
        // feature: changing fields
        frame.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (model.mouseMode.get() != null && event.getPickResult().getIntersectedNode().getClass().equals(Canvas.class)) {
                model.world.place(model.mouseMode.get(), (int) (event.getY() / tileSize()), (int) (event.getX() / tileSize()));
                draw();
            }
        });

        // feature: placing by dragging (all fields allowed)
        frame.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isPrimaryButtonDown()) {
                int row = tileBy(event.getY());
                int col = tileBy(event.getX());
                if (model.world.isFieldWithActor(row, col)) {
                    dragging = Field.ACTOR;
                } else if (model.world.isFieldWithStart(row, col)) {
                    dragging = Field.START;
                } else {
                    dragging = model.world.getField()[row][col];
                }
            }
        });
        frame.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (dragging != null) {
                int row = tileBy(event.getY());
                int col = tileBy(event.getX());
                model.world.place(dragging, row, col);
                draw();
            }
        });
        frame.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            dragging = null;
        });

        // feature: actors popup menu
        frame.setOnContextMenuRequested(event -> {
            int row = tileBy(event.getY());
            int col = tileBy(event.getX());
            if (model.world.isFieldWithActor(row, col)) {
                actorContextMenu.setX(event.getScreenX());
                actorContextMenu.setY(event.getScreenY());
                actorContextMenu.show(frame.getScene().getWindow());
            }
        });

        // feature: alt + mouse scroll can zomm in/out canvas
        scrollPane.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (event.isAltDown()) {
                if (0 > event.getDeltaY()) {
                    zoom -= 0.1*zoom;
                } else {
                    zoom += 0.1*zoom;
                }
                resize();
                draw();
            }
        });
    }

    void postInitialize() {
        // subscribe to model.world changes
        model.world.addObserver("view", this);

        // auto scrollbars for canvas
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        update();
    }

    @Override
    public void update() {
        buildActorContextMenu();
        resize();
        draw();
    }

    private void resize() {
        frame.setHeight(tileSize(model.world.sizeRow()));
        frame.setWidth(tileSize(model.world.sizeCol()));
    }

    private void draw() {
        // clean canvas
        gc.clearRect(0, 0, frame.getWidth(), frame.getHeight());

        Field[][] state = model.world.getField();
        //drawGrid(state);
        drawGridBorder(state);
        drawGround(state);
        drawFields(state);
    }

    private void drawGrid(Field[][] state) {
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[row].length; col++) {
                gc.setStroke(Color.GRAY);
                gc.setLineWidth(1);
                gc.strokeRect(tileSize(col), tileSize(row), tileSize(), tileSize());
            }
        }
    }
    private void drawGridBorder(Field[][] state) {
        gc.strokeRect(0, 0, tileSize(state.length), tileSize(state[0].length));
    }

    private void drawGround(Field[][] state) {
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[row].length; col++) {
                gc.drawImage(ground, tileSize(col), tileSize(row), tileSize(), tileSize());
            }
        }
    }

    private void drawFields(Field[][] state) throws InternalUnkownFieldException {
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[row].length; col++) {
                switch (state[row][col]) {
                    case START:
                        drawStartpoint(row, col);
                        break;
                    case ACTOR_AT_START:
                        drawStartpoint(row, col);
                        drawActor(row, col);
                        break;
                    case ACTOR:
                        drawActor(row, col);
                        break;
                    case ACTOR_ON_ITEM:
                        drawItem(row, col);
                        drawActor(row, col);
                        break;
                    case OBSTACLE:
                        drawObstacle(row, col);
                        break;
                    case ITEM:
                        drawItem(row, col);
                        break;
                    case FREE:
                        break;
                    default:
                        throw new InternalUnkownFieldException();
                }
            }
        }
    }


    private void drawStartpoint(int row, int col) {
        gc.drawImage(start, tileSize(col), tileSize(row), tileSize(), tileSize());
    }

    private void drawActor(int row, int col) {
        switch(model.world.getActorDir()) {
            case UP:
                gc.drawImage(actorU, tileSize(col), tileSize(row), tileSize(), tileSize());
                break;
            case DOWN:
                gc.drawImage(actorD, tileSize(col), tileSize(row), tileSize(), tileSize());
                break;
            case LEFT:
                gc.drawImage(actorL, tileSize(col), tileSize(row), tileSize(), tileSize());
                break;
            case RIGHT:
                gc.drawImage(actorR, tileSize(col), tileSize(row), tileSize(), tileSize());
                break;
        }

    }

    private void drawObstacle(int row, int col) {
        Image img = obstacle;
        if (model.world.isFieldAtBorder(row, col) && 0.8 < new Random().nextDouble()) {
            //img = obstacle_random // TODO add random obstacle ; like glass
        }
        gc.drawImage(img, tileSize(col), tileSize(row), tileSize(), tileSize());
    }

    private void drawItem(int row, int col) {
        gc.drawImage(item, tileSize(col), tileSize(row), tileSize(), tileSize());
    }

    private double tileSize() {
        return zoom * TILE_SIZE;
    }
    private double tileSize(double multiplikator) {
        return zoom * TILE_SIZE * multiplikator;
    }
    private int tileBy(double i) {
        return (int) (i / tileSize());
    }
}
