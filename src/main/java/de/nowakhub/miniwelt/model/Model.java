package de.nowakhub.miniwelt.model;

import de.nowakhub.miniwelt.controller.TabsController;
import javafx.beans.property.*;

import java.io.File;
import java.util.Random;

public class Model {

    public final TabsController tabsController;

    public File programFile;
    public final StringProperty program = new SimpleStringProperty();
    public final BooleanProperty programDirty = new SimpleBooleanProperty();
    public String programSave;

    public final World world;
    public final ObjectProperty<Field> mouseMode = new SimpleObjectProperty<>();

    public Actor actor;

    public final StringProperty statusText = new SimpleStringProperty();


    public Model(TabsController tabsController, File programFile, String program) {
        this.tabsController = tabsController;
        this.programFile = programFile;

        this.program.set(program);
        this.programDirty.set(false);
        this.programSave = program;
        if (programFile == null && program == null) {
            this.program.set("void main() {\n\tstepAhead();\n}");
            this.programDirty.set(true);
            this.programSave = null;
        }

        world = new World(10, 10);
        actor = new Actor(world);
        defaultWorld();
    }

    private void defaultWorld() {
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
