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
    private final DockerManager dockerManager;

    public SceneFactory(User user, AppMenu appMenu, DockerManager dockerManager) {
        this.user = user;
        this.appMenu = appMenu;
        this.dockerManager = dockerManager;
    }

    public Scene createScene(String sceneType) {
        switch (sceneType) {
            case "Home":
                HomeScene homeScene = new HomeScene(user, appMenu);
                HomeController homeController = new HomeController(homeScene, dockerManager);
                homeScene.setController(homeController);
                return homeScene.createScene();
            case "Docker":
                DockerScene dockerScene = new DockerScene(appMenu);
                DockerController dockerController = new DockerController(dockerScene, dockerManager);
                dockerScene.setController(dockerController);
                return dockerScene.createScene();
            default:
                throw new IllegalArgumentException("Invalid scene type: " + sceneType);
        }
    }
}