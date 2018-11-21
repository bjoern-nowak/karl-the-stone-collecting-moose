package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Field;
import de.nowakhub.miniwelt.model.Observer;
import de.nowakhub.miniwelt.model.World;
import de.nowakhub.miniwelt.model.exceptions.InternalUnkownFieldException;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Random;

public class ViewController extends SubController implements Observer<World> {

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

    private void addEventHandler() {
        // feature: changing fields
        frame.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (mouseMode.get() != null && event.getPickResult().getIntersectedNode().getClass().equals(Canvas.class)) {
                world.get().place(mouseMode.get(), (int) (event.getY() / tileSize()), (int) (event.getX() / tileSize()));
                draw();
            }
        });

        // feature: placing by dragging (all fields allowed)
        frame.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isPrimaryButtonDown()) {
                int row = tileBy(event.getY());
                int col = tileBy(event.getX());
                if (world.get().isFieldWithActor(row, col)) {
                    dragging = Field.ACTOR;
                } else if (world.get().isFieldWithStart(row, col)) {
                    dragging = Field.START;
                } else {
                    dragging = world.get().getField()[row][col];
                }
            }
        });
        frame.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (dragging != null) {
                int row = tileBy(event.getY());
                int col = tileBy(event.getX());
                world.get().place(dragging, row, col);
                draw();
            }
        });
        frame.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            dragging = null;
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

    public void postInitialize() {
        // subscribe to world changes
        world.get().addObserver("view", this);

        // auto scrollbars for canvas
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        update();
    }

    @Override
    public void update() {
        resize();
        draw();
    }

    private void resize() {
        frame.setHeight(tileSize(world.get().sizeRow()));
        frame.setWidth(tileSize(world.get().sizeCol()));
    }

    private void draw() {
        // clean canvas
        gc.clearRect(0, 0, frame.getWidth(), frame.getHeight());

        Field[][] state = world.get().getField();
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
        switch(world.get().getActorDir()) {
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
        if (world.get().isFieldAtBorder(row, col) && 0.8 < new Random().nextDouble()) {
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
