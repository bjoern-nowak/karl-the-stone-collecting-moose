package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.*;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

public class WorldController implements Observer {
    
    private Model model;

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

    private Image obstacle = new Image("/images/world/obstacle.png");
    private Image item = new Image("/images/world/item.png");
    private Image start = new Image("/images/world/bush.png");

    private Image actorU = new Image("/images/world/actor.png");
    private Image actorL = new Image("/images/world/actor.png");
    private Image actorD = new Image("/images/world/actor.png");
    private Image actorR = new Image("/images/world/actor.png");

    private Image grass_clean = new Image("/images/world/grass_clean.png");
    private Image grass_dirty = new Image("/images/world/grass_dirty.png");
    private Image grass_wild = new Image("/images/world/grass_wild.png");

    private Image corner_up_left = new Image("/images/world/border_up_left.png");
    private Image corner_up_right = new Image("/images/world/border_up_right.png");
    private Image corner_down_left = new Image("/images/world/border_down_left.png");
    private Image corner_down_right = new Image("/images/world/border_down_right.png");

    private Image border_up_clean = new Image("/images/world/border_up_clean.png");
    private Image border_up_wild = new Image("/images/world/border_up_wild.png");
    private Image border_left_clean = new Image("/images/world/border_left_clean.png");
    private Image border_left_wild = new Image("/images/world/border_left_wild.png");
    private Image border_down_clean = new Image("/images/world/border_down_clean.png");
    private Image border_down_wild = new Image("/images/world/border_down_wild.png");
    private Image border_right_clean = new Image("/images/world/border_right_clean.png");
    private Image border_right_wild = new Image("/images/world/border_right_wild.png");

    private Image water_clean = new Image("/images/world/water_clean.png");
    private Image water_wild = new Image("/images/world/water_wild.png");
    private Image water_shiny = new Image("/images/world/water_shiny.png");
    private Image water_dirt = new Image("/images/world/water_dirt.png");
    private Image water_grass = new Image("/images/world/water_grass.png");
    private Image water_rock = new Image("/images/world/water_rock.png");
    private Image water_leaf = new Image("/images/world/water_leaf.png");


    public void initialize() {
        // wrong: there is one world controller per model/tab
        // subscribe to change of model (tab switch)
        //ModelCtx.addObserver("" + this.hashCode(), this::postInitialize);
        
        gc = frame.getGraphicsContext2D();
        addEventHandler();
    }

    private void addEventHandler() {
        // only events on actual play field
        EventHandler<MouseEvent> OnlyEventsOnActualField = event -> {
            if (!model.getWorld().isInBoundary(tileBy(event.getY()), tileBy(event.getX()))) event.consume();
        };
        frame.addEventFilter(MouseEvent.MOUSE_RELEASED, OnlyEventsOnActualField);
        frame.addEventFilter(MouseEvent.MOUSE_PRESSED, OnlyEventsOnActualField);
        frame.addEventFilter(MouseEvent.MOUSE_DRAGGED, OnlyEventsOnActualField);

        // feature: changing fields
        frame.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (model.mouseMode.get() != null && event.getPickResult().getIntersectedNode().getClass().equals(Canvas.class)) {
                model.getWorld().place(model.mouseMode.get(), tileBy(event.getY()), tileBy(event.getX()));
            }
        });

        // feature: placing by dragging (all fields allowed)
        frame.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isPrimaryButtonDown()) {
                int row = tileBy(event.getY());
                int col = tileBy(event.getX());
                if (model.getWorld().isFieldWithActor(row, col)) {
                    dragging = Field.ACTOR;
                } else if (model.getWorld().isFieldWithStart(row, col)) {
                    dragging = Field.START;
                } else {
                    dragging = model.getWorld().getField()[row][col];
                }
            }
        });
        frame.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (dragging != null && event.getPickResult().getIntersectedNode().getClass().equals(Canvas.class)) {
                int row = tileBy(event.getY());
                int col = tileBy(event.getX());
                model.getWorld().place(dragging, row, col);
            }
        });
        frame.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> dragging = null);

        // feature: actors popup menu
        frame.setOnContextMenuRequested(event -> {
            int row = tileBy(event.getY());
            int col = tileBy(event.getX());
            if (model.getWorld().isFieldWithActor(row, col)) {
                actorContextMenu.setX(event.getScreenX());
                actorContextMenu.setY(event.getScreenY());
                actorContextMenu.show(frame.getScene().getWindow());
            }
        });

        // feature: ctrl + mouse scroll can zomm in/out canvas
        scrollPane.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
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

    void postInitialize(Model model) {
        // save model
        this.model = model;
        
        // sync canvas for actionController to make snapshots
        model.worldCanvas = frame;

        // subscribe to changes in the world
        model.getWorld().addObserver("world", this);

        // auto scrollbars for canvas
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

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
        frame.setHeight(tilePos(2) + tilePos(model.getWorld().getSizeRow()));
        frame.setWidth(tilePos(2) + tilePos(model.getWorld().getSizeCol()));
    }

    private void draw() {
        // clean canvas
        gc.clearRect(0, 0, frame.getWidth(), frame.getHeight());

        Field[][] state = model.getWorld().getField();
        drawGround(state);
        drawGroundBorder(state);
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

    private void drawGround(Field[][] state) {
        Random radomizer = new Random();
        
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[row].length; col++) {
                Image img = grass_clean;
                double random = radomizer.nextDouble();
                if (random < 0.3) img = grass_wild;
                else if (random < 0.6) img = grass_dirty;
                drawImage(grass_clean, col, row);
            }
        }
    }

    private void drawGroundBorder(Field[][] state) {

        for (int row = 0; row < state.length + 2; row++) {
            for (int col = 0; col < state[0].length + 2; col++) {
                Image img = null;

                boolean up = row == 0;
                boolean left = col == 0;
                boolean down = row == state.length + 1;
                boolean right = col == state[state.length - 1].length + 1;

                if (up && left)  img = corner_up_left;
                if (up && right)  img = corner_up_right;
                if (down && left)  img = corner_down_left;
                if (down && right)  img = corner_down_right;

                if (up && !left && !right)  img = border_up_clean;
                if (left && !up && !down)  img = border_left_clean;
                if (down && !left && !right)  img = border_down_clean;
                if (right && !up && !down)  img = border_right_clean;

                gc.drawImage(img, tilePos(col) - tileSize(), tilePos(row) - tileSize(), tileSize(), tileSize());
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
        drawImage(start, col, row);
    }

    private void drawActor(int row, int col) {
        switch(model.getWorld().getActorDir()) {
            case UP:
                drawImage(actorU, col, row);
                break;
            case DOWN:
                drawImage(actorD, col, row);
                break;
            case LEFT:
                drawImage(actorL, col, row);
                break;
            case RIGHT:
                drawImage(row, col);
                break;
        }

    }

    private void drawImage(int row, int col) {
        drawImage(actorR, col, row);
    }

    private void drawObstacle(int row, int col) {
        Image img = obstacle;
        //if (model.getWorld().isFieldAtBorder(row, col) && 0.8 < new Random().nextDouble()) {
        //    img = obstacle_random // TODO add random obstacle ; like glass
        //}
        drawImage(img, col, row);
    }

    private void drawItem(int row, int col) {
        drawImage(item, col, row);
    }
    
    private void drawImage(Image img, double col, double row) {
        gc.drawImage(img, tilePos(col), tilePos(row), tileSize(), tileSize());
    }

    private double tileSize() {
        return zoom * TILE_SIZE;
    }
    private double tilePos(double multiplikator) {
        return tileSize() + (tileSize() * multiplikator);
    }
    private int tileBy(double i) {
        return (int) (i / tileSize()) - 1;
    }
}
