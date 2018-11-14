package de.nowakhub.miniwelt.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class ViewController extends SubController {

    private final int TILE_SIZE = 32;

    @FXML
    private Canvas frame;
    private GraphicsContext gc;

    private Image wall = new Image("/images/world/wall_clean.png");


    public void initialize() {
        //frame.heightProperty().bind(world.get().sizeYProperty().multiply(TILE_SIZE));
        //frame.widthProperty().bind(world.get().sizeXProperty().multiply(TILE_SIZE));
        frame.setHeight(10 * TILE_SIZE);
        frame.setWidth(10 * TILE_SIZE);
        gc = frame.getGraphicsContext2D();
        draw();
    }

    private void draw() {
        gc.drawImage(wall, 0, 100, 0, 100);
        gc.setFill(Color.GREEN);
        gc.fillOval(0, 100, 25, 75);
    }

}
