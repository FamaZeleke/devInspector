package nivohub.devInspector.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class SceneController {
    private final Stage primaryStage;
    private final Map<String, Scene> scenes = new HashMap<>();

    // Functional Interface enforcing Single Abstract Method (SAM) rule
    @FunctionalInterface
    public interface SceneCreator {
        Scene createScene();
    }

    public SceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void addScene(String name, SceneCreator creator) {
        Scene scene = creator.createScene();
        scenes.put(name, scene);
    }

    public void showScene(String name) {
        Scene scene = scenes.get(name);
        if (scene != null) {
            primaryStage.setScene(scene);
        } else {
            throw new IllegalArgumentException("No scene with name: " + name);
        }
    }
}