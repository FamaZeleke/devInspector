package nivohub.devInspector.controller;

import javafx.scene.Scene;
import nivohub.devInspector.model.DockerManager;
import nivohub.devInspector.model.User;
import nivohub.devInspector.view.AppMenu;
import nivohub.devInspector.view.DockerScene;
import nivohub.devInspector.view.HomeScene;

public class SceneFactory {
    private final User user;
    private final AppMenu appMenu;

    public SceneFactory(User user, AppMenu appMenu) {
        this.user = user;
        this.appMenu = appMenu;
    }

    public Scene createScene(String sceneType) {
        switch (sceneType) {
            case "Home":
                return new HomeScene(user, appMenu).createScene();
            case "Docker":
                DockerManager dockerManager = new DockerManager();
                DockerScene dockerScene = new DockerScene(appMenu);
                DockerController dockerController = new DockerController(dockerScene, dockerManager);
                dockerScene.setController(dockerController);
                return dockerScene.createScene();
            default:
                throw new IllegalArgumentException("Invalid scene type: " + sceneType);
        }
    }
}