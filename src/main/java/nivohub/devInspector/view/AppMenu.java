package nivohub.devInspector.view;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import nivohub.devInspector.controller.SceneController;


public class AppMenu {
    private final SceneController sceneController;

    public AppMenu(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public MenuBar createMenu() {
        Menu mainMenu = new Menu("Main Menu");

        MenuItem home = new MenuItem("Home");
        home.setOnAction(event -> sceneController.showScene("Home"));

        MenuItem docker = new MenuItem("Docker");
        docker.setOnAction(event -> sceneController.showScene("Docker"));

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> System.exit(0));

        mainMenu.getItems().addAll(home, docker, exit);

        return new MenuBar(mainMenu);
    }
}

