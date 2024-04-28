package nivohub.devInspector.controller;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import nivohub.devInspector.model.DockerManager;
import nivohub.devInspector.model.User;
import nivohub.devInspector.view.AppMenu;
import nivohub.devInspector.view.DockerScene;
import nivohub.devInspector.view.Home;
import nivohub.devInspector.view.LoginScene;
import nivohub.devInspector.exceptions.FullNameException;
import nivohub.devInspector.exceptions.PasswordException;

public class LoginController {
    private LoginScene view;
    private User model;
    private SceneController sceneController;

    public LoginController(LoginScene view, User model, SceneController sceneController) {
        this.view = view;
        this.model = model;
        this.sceneController = sceneController;
    }

    public void validateAndSubmit(String fullName, String password) throws FullNameException, PasswordException {
        model.setFullName(fullName);
        model.validatePassword(password);
    }

    public void handleSubmit() {
        try {
            validateAndSubmit(view.getFullNameInput(), view.getPasswordInput());

            // User has logged in successfully, create and add other scenes to the sceneController
            Home home = new Home(model, new AppMenu(sceneController));
            sceneController.addScene("Home", home);

            DockerManager dockerManager = new DockerManager();
            DockerScene dockerScene = new DockerScene(new AppMenu(sceneController));
            DockerController dockerController = new DockerController(dockerScene, dockerManager);
            dockerScene.setController(dockerController);
            sceneController.addScene("Docker", dockerScene);

            // Show the home scene
            sceneController.showScene("Home");
        } catch (FullNameException | PasswordException e) {
            view.setErrorMessage(e.getMessage());
        } catch (DockerCertificateException e) {
            throw new RuntimeException(e);
        }
    }
}
