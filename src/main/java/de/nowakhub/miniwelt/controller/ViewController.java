package de.nowakhub.miniwelt.controller;

import de.nowakhub.miniwelt.exceptions.InternalUnkownFieldException;
import de.nowakhub.miniwelt.model.Field;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ViewController extends SubController {

    private double zoom = 1.0;
    private final int TILE_SIZE = 32;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private StackPane stackPane;

    @FXML
    private Canvas frame;
    private GraphicsContext gc;

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

        // add feature: changing fields
        frame.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getPickResult().getIntersectedNode().getClass().equals(Canvas.class)) {
                world.get().place(mouseMode.get(), (int) (event.getX() / tileSize()), (int) (event.getY() / tileSize()));
                draw();
            }
        });

        // add feature: alt + mouse scroll can zomm in/out canvas
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
        /*if (0.8 < new Random().nextDouble()) {
            // TODO only on border [(0,i) (i,0) (maxX,i) (i,maxY)]
            // TODO not in border corners [(0,0) (0,maxY) (maxX,0) (maxX,maxY)]
            img = glass;
        }*/
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

}
