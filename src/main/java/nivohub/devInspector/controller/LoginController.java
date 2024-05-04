package nivohub.devInspector.controller;

import nivohub.devInspector.model.User;
import nivohub.devInspector.view.AppMenu;
import nivohub.devInspector.view.LoginScene;
import javafx.scene.Scene;
import nivohub.devInspector.exceptions.FullNameException;
import nivohub.devInspector.exceptions.PasswordException;

public class LoginController {
    private final LoginScene view;
    private final User user;
    private final SceneController sceneController;

    public LoginController(LoginScene view, User model, SceneController sceneController) {
        this.view = view;
        this.user = model;
        this.sceneController = sceneController;
    }

    public void validateAndSubmit(String fullName, String password) throws FullNameException, PasswordException {
        user.setFullName(fullName);
        user.validatePassword(password);
    }

    public void handleSubmit() {
        try {
            validateAndSubmit(view.getFullNameInput(), view.getPasswordInput());

            AppMenu appMenu = new AppMenu(sceneController);
            SceneFactory sceneFactory = new SceneFactory(user, appMenu);
            Scene homeScene = sceneFactory.createScene("Home");
            sceneController.addScene("Home", homeScene);

            Scene dockerScene = sceneFactory.createScene("Docker");
            sceneController.addScene("Docker", dockerScene);

            // Show the home scene
            sceneController.showScene("Home");
        } catch (FullNameException | PasswordException e) {
            view.setErrorMessage(e.getMessage());
        }
    }
}
