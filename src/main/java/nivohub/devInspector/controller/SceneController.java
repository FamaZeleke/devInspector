package nivohub.devInspector.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class SceneController {
    private final Stage primaryStage;
    private final Map<String, Scene> scenes = new HashMap<>();

    public SceneController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void addScene(String name, Scene scene) {
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