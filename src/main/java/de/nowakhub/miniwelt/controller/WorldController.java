package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.Alerts;
import de.nowakhub.miniwelt.controller.util.Images;
import de.nowakhub.miniwelt.model.*;
import de.nowakhub.miniwelt.model.interfaces.Invisible;
import de.nowakhub.miniwelt.model.interfaces.Observer;
import de.nowakhub.miniwelt.model.exceptions.InternalUnkownFieldException;
import javafx.event.EventHandler;
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
import java.util.*;
import java.util.stream.Collectors;

public class WorldController implements Observer {
    
    private Model model;
    private Map<String, Double> randomGrass = new HashMap<>();
    private Map<String, Double> randomWater = new HashMap<>();
    private Random radomizer = new Random();

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private Canvas canvas;
    private GraphicsContext gc;

    private final int TILE_SIZE = 32;
    private double zoom = 1.0;
    private Field dragging;
    private ContextMenu actorContextMenu;

    public void initialize() {
        // wrong: there is one world controller per model/tab
        // subscribe to change of model (tab switch)
        //ModelCtx.addObserver("" + this.hashCode(), this::postInitialize);
        
        gc = canvas.getGraphicsContext2D();
        addEventHandler();

        // auto scrollbars for canvas
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
    }

    private void addEventHandler() {
        // only events on actual play field
        EventHandler<MouseEvent> OnlyEventsOnActualField = event -> {
            if (!model.getWorld().isInBoundary(tileBy(event.getY()), tileBy(event.getX()))) event.consume();
        };
        canvas.addEventFilter(MouseEvent.MOUSE_RELEASED, OnlyEventsOnActualField);
        canvas.addEventFilter(MouseEvent.MOUSE_PRESSED, OnlyEventsOnActualField);
        canvas.addEventFilter(MouseEvent.MOUSE_DRAGGED, OnlyEventsOnActualField);

        // feature: changing fields
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (dragging == null && model.mouseMode.get() != null && event.getPickResult().getIntersectedNode().getClass().equals(Canvas.class)) {
                model.getWorld().place(model.mouseMode.get(), tileBy(event.getY()), tileBy(event.getX()));
            }
        });

        // feature: placing by dragging (all fields allowed)
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.isPrimaryButtonDown()) {
                if (dragging == null) {
                    int row = tileBy(event.getY());
                    int col = tileBy(event.getX());
                    Field field = model.getWorld().getField()[row][col];
                    if (model.getWorld().isFieldWithActor(row, col)) {
                        field = Field.ACTOR;
                    } else if (model.getWorld().isFieldWithStart(row, col)) {
                        field = Field.START;
                    }
                    dragging = field;
                } else { // if (event.getPickResult().getIntersectedNode().getClass().equals(Canvas.class)) {
                    int row = tileBy(event.getY());
                    int col = tileBy(event.getX());
                    model.getWorld().place(dragging, row, col);
                }
            }
        });
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> dragging = null);

        // feature: actors popup menu
        canvas.setOnContextMenuRequested(event -> {
            int row = tileBy(event.getY());
            int col = tileBy(event.getX());
            if (model.getWorld().isFieldWithActor(row, col)) {
                actorContextMenu.setX(event.getScreenX());
                actorContextMenu.setY(event.getScreenY());
                actorContextMenu.show(canvas.getScene().getWindow());
            }
        });

        // feature: ctrl + mouse scroll can zomm in/out canvas
        scrollPane.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                if (0 > event.getDeltaY()) {
                    zoom -= 0.1 * zoom;
                } else {
                    zoom += 0.1 * zoom;
                }
                resize();
                draw();
            }
        });
    }

    void postInitialize(Model model) {
        // save model
        this.model = model;
        
        // sync canvas for actionController to make snapshots
        model.worldCanvas = canvas;

        // subscribe to changes in the world
        model.getWorld().addObserver("world", this);

        //draw world
        update();
    }

    @Override
    public void update() {
        buildActorContextMenu();
        resize();
        draw();
    }

    private void buildActorContextMenu() {
        actorContextMenu = new ContextMenu();
        actorContextMenu.setAutoFix(true);

        // for better user experience: add separators between specific method groups
        addMethodsToActorContextMenu(Arrays.asList("stepAhead", "turnRight", "pickUp", "dropDown"));
        addMethodsToActorContextMenu(Arrays.asList("aheadClear", "bagEmpty", "foundItem", "atStart"));
        Class<? extends Actor> cls = model.getActor().getClass();
        if (!cls.equals(Actor.class)) {
            Arrays.stream(cls.getDeclaredMethods()).forEach(this::addMethodToActorContextMenu);
        }
    }

    private void addMethodsToActorContextMenu(Collection<String> methods) {
        methods.forEach(method -> {
            try {
                Method m = model.getActor().getClass().getMethod(method);
                addMethodToActorContextMenu(m);
            } catch (NoSuchMethodException ex) {
                Alerts.showException(ex);
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
                        method.setAccessible(true);
                        Object result = method.invoke(model.getActor());
                        if (result != null) { // instead of instanceOf check to enable various return types
                            Alerts.showInfo(method.getName() + "()", "" + result);
                        }
                    } catch (Exception ex) {
                        Alerts.showException(ex);
                    }
                });
            }
            actorContextMenu.getItems().add(item);
        }
    }

    private void resize() {
        canvas.setHeight(tilePos(0) + tilePos(model.getWorld().getSizeRow()));
        canvas.setWidth(tilePos(0) + tilePos(model.getWorld().getSizeCol()));
    }

    private void draw() {
        // clean canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        Field[][] state = model.getWorld().getField();
        drawWater(state);
        drawGround(state);
        drawFields(state);
        //drawGrid(state);
    }

    private void drawGrid(Field[][] state) {
        // TODO OLD: need a fix
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[row].length; col++) {
                gc.setStroke(Color.GRAY);
                gc.setLineWidth(1);
                gc.strokeRect(tilePos(col), tilePos(row), tileSize(), tileSize());
            }
        }
    }

    private void drawWater(Field[][] state) {

        for (int row = 0; row < state.length + 4; row++) {
            for (int col = 0; col < state[0].length + 4; col++) {
                boolean isOuter = row == 0 || col == 0 || row == state.length + 3  || col == state[0].length + 3;
                boolean isInner = row == 1 || col == 1 || row == state.length + 2  || col == state[0].length + 2;

                if (isOuter || isInner) {

                    String key = row + "" + col;
                    if (!randomWater.containsKey(key)) randomWater.put(key, radomizer.nextDouble());
                    Double random = randomWater.get(key);
                    Image img = null;

                    if (isOuter) {
                        if (random < 0.7) img = Images.water_clean;
                        else if (random < 0.75) img = Images.water_dirt;
                        else if (random < 0.8) img = Images.water_grass;
                        else if (random < 0.85) img = Images.water_leaf;
                        else if (random < 0.9) img = Images.water_rock;
                        else if (random < 0.95) img = Images.water_shiny;
                        else if (random < 1.0) img = Images.water_wild;

                    } else {
                        boolean up = row == 1;
                        boolean left = col == 1;
                        boolean down = row == state.length + 2;
                        boolean right = col == state[0].length + 2;

                        if (up && left)  img = Images.corner_up_left;
                        else if (up && right)  img = Images.corner_up_right;
                        else if (down && left)  img = Images.corner_down_left;
                        else if (down && right)  img = Images.corner_down_right;

                        if (random < 0.8) {
                            if (up && !left && !right)  img = Images.border_up_clean;
                            else if (left && !up && !down)  img = Images.border_left_clean;
                            else if (down && !left && !right)  img = Images.border_down_clean;
                            else if (right && !up && !down)  img = Images.border_right_clean;
                        } else {
                            if (up && !left && !right)  img = Images.border_up_wild;
                            else if (left && !up && !down)  img = Images.border_left_wild;
                            else if (down && !left && !right)  img = Images.border_down_wild;
                            else if (right && !up && !down)  img = Images.border_right_wild;
                        }
                    }

                    gc.drawImage(img, tilePos(col) - (tileSize() * 2), tilePos(row) - (tileSize() * 2), tileSize(), tileSize());
                }
            }
        }
    }

    private void drawGround(Field[][] state) {
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[row].length; col++) {
                Image img = Images.grass_clean;
                String key = row + "" + col;
                if (!randomGrass.containsKey(key)) randomGrass.put(key, radomizer.nextDouble());
                if (randomGrass.get(key) < 0.2) img = Images.grass_wild;
                else if (randomGrass.get(key) < 0.25) img = Images.grass_dirty;
                drawImage(img, col, row);
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
                        drawObstacle(row, col, state);
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
        drawImage(Images.start, col, row);
    }

    private void drawActor(int row, int col) {
        Image img;
        switch(model.getWorld().getActorDir()) {
            case UP:
                img = Images.actor_up;
                break;
            case DOWN:
                img = Images.actor_down;
                break;
            case LEFT:
                img = Images.actor_left;
                break;
            default:
                img = Images.actor_right;
        }
        // draw actor bigger so it gets an isometric look & feel
        gc.drawImage(img, tilePos(col) - (tileSize() / 2), tilePos(row) - (tileSize() / 1.8), tileSize() * 1.8, tileSize() * 1.8);
    }

    private void drawObstacle(int row, int col, Field[][] state) {
        Image img = Images.obstacle;

        boolean left = model.getWorld().isInBoundary(row, col - 1) && state[row][col - 1].hasObstacle();
        boolean down = model.getWorld().isInBoundary(row + 1, col) && state[row + 1][col].hasObstacle();
        boolean right = model.getWorld().isInBoundary(row, col + 1) && state[row][col + 1].hasObstacle();

        if (left && down && right) img = Images.obstacle_left_down_right;
        else if (left && down) img = Images.obstacle_left_down;
        else if (left && right) img = Images.obstacle_left_right;
        else if (down && right) img = Images.obstacle_down_right;
        else if (left) img = Images.obstacle_left;
        else if (down) img = Images.obstacle_down;
        else if (right) img = Images.obstacle_right;

        drawImage(img, col, row);
    }

    private void drawItem(int row, int col) {
        drawImage(Images.item, col, row);
    }


    private void drawImage(int row, int col) {
        drawImage(Images.actor_right, col, row);
    }

    private void drawImage(Image img, double col, double row) {
        gc.drawImage(img, tilePos(col), tilePos(row), tileSize(), tileSize());
    }

    private double tileSize() {
        return zoom * TILE_SIZE;
    }
    private double tilePos(double multiplikator) {
        return tileSize() * (multiplikator + 2);
    }
    private int tileBy(double i) {
        return (int) (i / tileSize()) - 2;
    }
}
