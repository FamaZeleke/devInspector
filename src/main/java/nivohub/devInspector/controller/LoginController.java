package nivohub.devInspector.controller;

import javafx.scene.control.ButtonType;
import nivohub.devInspector.exceptions.DockerNotRunningException;
import nivohub.devInspector.model.DockerManager;
import nivohub.devInspector.model.User;
import nivohub.devInspector.view.AlertDialog;
import nivohub.devInspector.view.AppMenu;
import nivohub.devInspector.view.LoginScene;
import javafx.scene.Scene;
import nivohub.devInspector.exceptions.FullNameException;
import nivohub.devInspector.exceptions.PasswordException;

import java.util.Optional;

public class LoginController {
    private final LoginScene view;
    private final User user;
    private final SceneController sceneController;
    private final CommandLineController commandLineController;
    private final DockerManager dockerManager = new DockerManager();
    private final AlertDialog alertDialog = new AlertDialog();

    public LoginController(LoginScene view, User model, SceneController sceneController, CommandLineController commandLineController) {
        this.view = view;
        this.user = model;
        this.sceneController = sceneController;
        this.commandLineController= commandLineController;
    }

    public void validateAndSubmit(String fullName, String password) throws FullNameException, PasswordException {
        user.setFullName(fullName);
        user.validatePassword(password);
    }

    public void handleSubmit() {
        while (true) {
            try {
                checkDockerRunning();
                break;
            } catch (DockerNotRunningException e) {
                view.setErrorMessage("Try again : " + e.getMessage());
                // Docker is not running, so show the dialog
                Optional<ButtonType> result;
                do {
                    result = alertDialog.showErrorDialog("Docker Not Running", "Docker is not running", "Please start Docker and try again");
                } while (result.get() == ButtonType.OK && !dockerManager.isDockerRunning());

                if (result.get() != ButtonType.OK) {
                    // User didn't click OK, so return
                    return;
                }
            }
        }

        try {
            validateAndSubmit(view.getFullNameInput(), view.getPasswordInput());
            constructScenes();
        } catch (FullNameException | PasswordException e) {
            view.setErrorMessage(e.getMessage());
        }
    }

    private void constructScenes() {
        AppMenu appMenu = new AppMenu(sceneController, commandLineController);
        SceneFactory sceneFactory = new SceneFactory(user, appMenu, dockerManager);
        Scene homeScene = sceneFactory.createScene("Home");
        sceneController.addScene("Home", homeScene);

        Scene dockerScene = sceneFactory.createScene("Docker");
        sceneController.addScene("Docker", dockerScene);

        // Show the home scene
        sceneController.showScene("Home");
    }

    public void checkDockerRunning() throws DockerNotRunningException {
        boolean isDockerRunning = dockerManager.isDockerRunning();
        if (!isDockerRunning) {
            throw new DockerNotRunningException();
        }
    }

}
