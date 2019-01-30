package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.controller.util.Alerts;
import de.nowakhub.miniwelt.controller.util.Images;
import de.nowakhub.miniwelt.model.Actor;
import de.nowakhub.miniwelt.model.util.Field;
import de.nowakhub.miniwelt.model.Model;
import de.nowakhub.miniwelt.model.exceptions.InternalUnkownFieldException;
import de.nowakhub.miniwelt.model.util.Invisible;
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Controller for the right side of a tab (the world)
 * Contained in a tab; works directly on a model
 */
public class WorldController {

    private Model model;

    // used for variations of graphics
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
                    Field field = model.getWorld().getField(row, col);

                    // dont drag stacked objects (like Actor over an item)
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

        // feature: show actors context menu
        canvas.setOnContextMenuRequested(event -> {
            int row = tileBy(event.getY());
            int col = tileBy(event.getX());
            if (model.getWorld().isFieldWithActor(row, col)) {
                actorContextMenu.setX(event.getScreenX());
                actorContextMenu.setY(event.getScreenY());
                actorContextMenu.show(canvas.getScene().getWindow());
            }
        });

        // feature: ctrl + mousewheel can zoom in/out canvas
        stackPane.addEventHandler(ScrollEvent.SCROLL, event -> {
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

        // subscribe to changes in the model
        model.addObserver("canvas", this::updateModel);

        // initial draw of world
        updateModel();
    }

    /**
     * add listener to (maybe new) world and call {@link #updateCanvas()}
     */
    private void updateModel() {
        // subscribe to changes in the world
        model.getWorld().addObserver("canvas", this::updateCanvas);

        // draw of world
        updateCanvas();
    }

    /**
     * based on current model: build new actor context menu; resize and draw canvas
     */
    private void updateCanvas() {
        buildActorContextMenu(); // TODO [error] maybe throws ConcurrentModificationException
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
        // filter method to include
        if (method.getAnnotation(Invisible.class) == null
                && !Modifier.isAbstract(modifiers)
                && !Modifier.isStatic(modifiers)
                && !Modifier.isPrivate(modifiers)) {

            // contreuct menu item text and action
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
                    // execute action silently
                    model.getWorld().silently(() -> {
                        method.setAccessible(true);
                        // call actor method
                        Object result = method.invoke(model.getActor());
                        if (result != null) {
                            // TODO [refactoring] handle return values better
                            Alerts.showInfo(method.getName() + "()", "" + result);
                        }
                        return null;
                    });
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
        drawWaterAndCliffs(state);
        drawGround(state);
        drawObjects(state);
        //drawGrid(state);
    }

    @Deprecated
    private void drawGrid(Field[][] state) {
        // TODO [refactoring/feature] method to show grid lines is deprecated
        /*
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[row].length; col++) {
                gc.setStroke(Color.GRAY);
                gc.setLineWidth(1);
                gc.strokeRect(tilePos(col), tilePos(row), tileSize(), tileSize());
            }
        }
        */
    }

    /**
     * draw the outer non-playable fields (water and cliffs)
     */
    private void drawWaterAndCliffs(Field[][] state) {
        // two more fields on each side of the state makes 4
        for (int row = 0; row < state.length + 4; row++) {
            for (int col = 0; col < state[0].length + 4; col++) {
                // convenient flags
                boolean isMostOuterRing = row == 0 || col == 0 || row == state.length + 3  || col == state[0].length + 3;
                boolean isOuterRing = row == 1 || col == 1 || row == state.length + 2  || col == state[0].length + 2;

                // only work on the outer rings
                if (isMostOuterRing || isOuterRing) {

                    // persist randomness
                    String key = row + "" + col;
                    if (!randomWater.containsKey(key)) randomWater.put(key, radomizer.nextDouble());
                    Double random = randomWater.get(key);
                    Image img = null;

                    // field should be water
                    if (isMostOuterRing) {
                        if (random < 0.7) img = Images.water_clean;
                        else if (random < 0.75) img = Images.water_dirt;
                        else if (random < 0.8) img = Images.water_grass;
                        else if (random < 0.85) img = Images.water_leaf;
                        else if (random < 0.9) img = Images.water_rock;
                        else if (random < 0.95) img = Images.water_shiny;
                        else if (random < 1.0) img = Images.water_wild;

                    // field should be a cliff
                    } else {
                        // convenient flags
                        boolean up = row == 1;
                        boolean left = col == 1;
                        boolean down = row == state.length + 2;
                        boolean right = col == state[0].length + 2;

                        // field is a corner
                        if (up && left)  img = Images.corner_up_left;
                        else if (up && right)  img = Images.corner_up_right;
                        else if (down && left)  img = Images.corner_down_left;
                        else if (down && right)  img = Images.corner_down_right;
                        else {
                            // field is a border
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
                    }

                    // finally draw it
                    gc.drawImage(img, tilePos(col) - (tileSize() * 2), tilePos(row) - (tileSize() * 2), tileSize(), tileSize());
                }
            }
        }
    }

    /**
     * draw ground of playable fields
     */
    private void drawGround(Field[][] state) {
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[row].length; col++) {
                // persist randomness
                String key = row + "" + col;
                if (!randomGrass.containsKey(key)) randomGrass.put(key, radomizer.nextDouble());

                // draw grass
                Image img = Images.grass_clean;
                if (randomGrass.get(key) < 0.2) img = Images.grass_wild;
                else if (randomGrass.get(key) < 0.25) img = Images.grass_dirty;
                drawImage(img, col, row);
            }
        }
    }

    /**
     * draw objects of playfield like actor and fences
     */
    private void drawObjects(Field[][] state) throws InternalUnkownFieldException {
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

    /**
     * draw actor; in correct direction; and a little bit bigger then others
     */
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

    /**
     * draw fence; connect neighbor fences
     */
    private void drawObstacle(int row, int col, Field[][] state) {
        Image img = Images.obstacle;

        // convenient flags
        boolean left = model.getWorld().isInBoundary(row, col - 1) && state[row][col - 1].hasObstacle();
        boolean down = model.getWorld().isInBoundary(row + 1, col) && state[row + 1][col].hasObstacle();
        boolean right = model.getWorld().isInBoundary(row, col + 1) && state[row][col + 1].hasObstacle();

        // connect neighbor fences
        if (left && down && right) img = Images.obstacle_left_down_right;
        else if (left && down) img = Images.obstacle_left_down;
        else if (left && right) img = Images.obstacle_left_right;
        else if (down && right) img = Images.obstacle_down_right;
        else if (left) img = Images.obstacle_left;
        else if (down) img = Images.obstacle_down;
        else if (right) img = Images.obstacle_right;

        // draw fence
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

    /**
     * define position for drawing; add extra space due to outer rings
     */
    private double tilePos(double multiplikator) {
        return tileSize() * (multiplikator + 2);
    }

    /**
     * get position of drawing; subtract extra space due to outer rings
     */
    private int tileBy(double i) {
        return (int) (i / tileSize()) - 2;
    }
}
