package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.controller.TabsController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.util.Random;

public class Model {

    public TabsController tabsController;

    public File programFile;

    public final World world;
    public final ObjectProperty<Field> mouseMode = new SimpleObjectProperty<>();
    
    public final StringProperty programText = new SimpleStringProperty();
    public final StringProperty statusText = new SimpleStringProperty();

    public Model() {
        world = new World(10, 10);
        world.placeStart(2,2);
        world.placeActor(2,2);

        for (int x = 0; x < world.sizeRow(); x++) {
            for (int y = 0; y < world.sizeCol(); y++) {
                if (world.isFieldAtBorder(x, y)) {
                    world.placeObstacle(x,y);
                } else {
                    double random = new Random().nextDouble();
                    if (random < 0.2) {
                        world.placeObstacle(x,y);
                    } else if (random < 0.3) {
                        world.placeItem(x,y);
                    }
                }
            }
        }
    }
}
