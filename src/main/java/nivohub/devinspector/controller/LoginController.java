package nivohub.devinspector.controller;

import javafx.concurrent.Task;
import nivohub.devinspector.exceptions.PasswordException;
import nivohub.devinspector.interactor.LoginInteractor;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.LoginViewBuilder;

public class LoginController extends BaseController{
    private LoginInteractor interactor;
    private final ApplicationController applicationController;

    public LoginController(UserModel model, ApplicationController applicationController) {
        interactor = new LoginInteractor(model);
        viewBuilder = new LoginViewBuilder(model, this::loginUser);
        this.applicationController = applicationController;
    }

    private void loginUser() {
        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws PasswordException {
                return interactor.attemptLogin();
            }
        };
        loginTask.setOnSucceeded(e -> {if (Boolean.TRUE.equals(loginTask.getValue())){
            applicationController.loadMainView();
        }
        });
        loginTask.setOnFailed(e -> {
            // Handle error
            e.getSource().getException().printStackTrace();
        });
        Thread loginThread = new Thread(loginTask);
        loginThread.start();
    };



//    public Scene getScene() {
//        return view;
//    }
//
//    public void validateAndSubmit(String fullName, String password) throws FullNameException, PasswordException {
//        user.setFullName(fullName);
//        user.validatePassword(password);
//    }
//
//    public void handleSubmit() {
//        while (true) {
//            try {
//                checkDockerRunning();
//                break;
//            } catch (DockerNotRunningException e) {
//                view.setErrorMessage("Try again : " + e.getMessage());
//                // Docker is not running, so show the dialog
//                Optional<ButtonType> result;
//                do {
//                    result = alertDialog.showErrorDialog("Docker Not Running", "Docker is not running", "Please start Docker and try again");
//                } while (result.get() == ButtonType.OK && !dockerManager.isDockerRunning());
//
//                if (result.get() != ButtonType.OK) {
//                    // User didn't click OK, so return
//                    return;
//                }
//            }
//        }
//
//        try {
//            validateAndSubmit(view.getFullNameInput(), view.getPasswordInput());
//            constructScenes();
//        } catch (FullNameException | PasswordException e) {
//            view.setErrorMessage(e.getMessage());
//        }
//    }
//
//    private void constructScenes() {
//        AppMenu appMenu = new AppMenu(sceneController, commandLineController);
//        SceneFactory sceneFactory = new SceneFactory(user, appMenu, dockerManager);
//        Scene homeScene = sceneFactory.createScene("Home");
//        sceneController.addScene("Home", homeScene);
//
//        Scene dockerScene = sceneFactory.createScene("Docker");
//        sceneController.addScene("Docker", dockerScene);
//
//        // Show the home scene
//        sceneController.showScene("Home");
//    }
//
//    public void checkDockerRunning() throws DockerNotRunningException {
//        boolean isDockerRunning = dockerManager.isDockerRunning();
//        if (!isDockerRunning) {
//            throw new DockerNotRunningException();
//        }
//    }

}
