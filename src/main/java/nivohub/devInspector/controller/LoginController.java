package nivohub.devInspector.controller;

import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import nivohub.devInspector.exceptions.PasswordException;
import nivohub.devInspector.interactor.LoginInteractor;
import nivohub.devInspector.model.UserModel;
import nivohub.devInspector.view.LoginViewBuilder;

public class LoginController {
//    private final LoginScene view;
//    private final User user;
//    private final AlertDialog alertDialog = new AlertDialog();
    private Builder<Region> viewBuilder;
    private LoginInteractor interactor;

    public LoginController() {
        UserModel model = new UserModel();
        interactor = new LoginInteractor(model);
        viewBuilder = new LoginViewBuilder(model, this::loginUser);
    }

    private void loginUser() {
        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws PasswordException {
                return interactor.attemptLogin();
            }
        };
        loginTask.setOnSucceeded(e -> {loginTask.getValue();});
        loginTask.setOnFailed(e -> {
            // Handle error
            e.getSource().getException().printStackTrace();
        });
        Thread loginThread = new Thread(loginTask);
        loginThread.start();
    };

    public Region getView(){
        return viewBuilder.build();
    }


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
