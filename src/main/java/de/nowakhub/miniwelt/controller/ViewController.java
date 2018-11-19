package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.model.Field;
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

public class ViewController extends SubController {

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
                world.get().place(mouseMode.get(), (int) (event.getX() / tileSize()), (int) (event.getY() / tileSize()));
                draw();
            }
        });

        // feature: placing by dragging (all fields allowed)
        frame.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isPrimaryButtonDown()) {
                int x = tileBy(event.getX());
                int y = tileBy(event.getY());
                if (world.get().hasActor(x, y)) {
                    dragging = Field.ACTOR;
                } else if (world.get().hasStart(x, y)) {
                    dragging = Field.START;
                } else {
                    dragging = world.get().state()[x][y];
                }
            }
        });
        frame.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (dragging != null) {
                int x = tileBy(event.getX());
                int y = tileBy(event.getY());
                world.get().place(dragging, x, y);
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
        // auto scrollbars for canvas
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        resize();
        draw();
    }

    private void resize() {
        frame.setHeight(tileSize(world.get().getSizeY()));
        frame.setWidth(tileSize(world.get().getSizeX()));
    }

    private void draw() {
        // clean canvas
        gc.clearRect(0, 0, frame.getWidth(), frame.getHeight());

        Field[][] state = world.get().state();
        //drawGrid(state);
        drawGridBorder(state);
        drawGround(state);
        drawFields(state);
    }

    private void drawGrid(Field[][] state) {
        for (int x = 0; x < state.length; x++) {
            for (int y = 0; y < state[x].length; y++) {
                gc.setStroke(Color.GRAY);
                gc.setLineWidth(1);
                gc.strokeRect(tileSize(x), tileSize(y), tileSize(), tileSize());
            }
        }
    }
    private void drawGridBorder(Field[][] state) {
        gc.strokeRect(0, 0, tileSize(state.length), tileSize(state[0].length));
    }

    private void drawGround(Field[][] state) {
        for (int x = 0; x < state.length; x++) {
            for (int y = 0; y < state[x].length; y++) {
                gc.drawImage(ground, tileSize(x), tileSize(y), tileSize(), tileSize());
            }
        }
    }

    private void drawFields(Field[][] state) throws InternalUnkownFieldException {
        for (int x = 0; x < state.length; x++) {
            for (int y = 0; y < state[x].length; y++) {
                switch (state[x][y]) {
                    case START:
                        drawStartpoint(x, y);
                        break;
                    case ACTOR_AT_START:
                        drawStartpoint(x, y);
                        drawActor(x, y);
                        break;
                    case ACTOR:
                        drawActor(x, y);
                        break;
                    case ACTOR_ON_ITEM:
                        drawItem(x, y);
                        drawActor(x, y);
                        break;
                    case OBSTACLE:
                        drawObstacle(x, y);
                        break;
                    case ITEM:
                        drawItem(x, y);
                        break;
                    case FREE:
                        break;
                    default:
                        throw new InternalUnkownFieldException();
                }
            }
        }
    }


    private void drawStartpoint(int x, int y) {
        gc.drawImage(start, tileSize(x), tileSize(y), tileSize(), tileSize());
    }

    private void drawActor(int x, int y) {
        switch(actor.get().dir) {
            case UP:
                gc.drawImage(actorU, tileSize(x), tileSize(y), tileSize(), tileSize());
                break;
            case DOWN:
                gc.drawImage(actorD, tileSize(x), tileSize(y), tileSize(), tileSize());
                break;
            case LEFT:
                gc.drawImage(actorL, tileSize(x), tileSize(y), tileSize(), tileSize());
                break;
            case RIGHT:
                gc.drawImage(actorR, tileSize(x), tileSize(y), tileSize(), tileSize());
                break;
        }

    }

    private void drawObstacle(int x, int y) {
        Image img = obstacle;
        if (world.get().isBorder(x, y) && 0.8 < new Random().nextDouble()) {
            //img = obstacle_random // TODO add random obstacle ; like glass
        }
        gc.drawImage(img, tileSize(x), tileSize(y), tileSize(), tileSize());
    }

    private void drawItem(int x, int y) {
        gc.drawImage(item, tileSize(x), tileSize(y), tileSize(), tileSize());
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
