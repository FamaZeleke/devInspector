package nivohub.devInspector.view;

import javafx.scene.control.*;
import nivohub.devInspector.controller.CommandLineController;
import nivohub.devInspector.controller.SceneController;


public class AppMenu {
    private final SceneController sceneController;
    private final CommandLineController commandLineController;

    public AppMenu(SceneController sceneController, CommandLineController commandLineController) {
        this.sceneController = sceneController;
        this.commandLineController= commandLineController;
    }

    public MenuBar createMenu() {
        Menu mainMenu = new Menu("Main Menu");
        Button cliButton = new Button("Command Line Tools");
        cliButton.setOnAction(event -> commandLineController.runMenuInTerminal());
        CustomMenuItem cliMenuItem = new CustomMenuItem(cliButton);
        cliMenuItem.setHideOnClick(false);

        MenuItem home = new MenuItem("Home");
        home.setOnAction(event -> sceneController.showScene("Home"));

        MenuItem docker = new MenuItem("Docker");
        docker.setOnAction(event -> sceneController.showScene("Docker"));

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> System.exit(0));

        mainMenu.getItems().addAll(home, docker, exit, cliMenuItem);

        return new MenuBar(mainMenu);
    }

}

