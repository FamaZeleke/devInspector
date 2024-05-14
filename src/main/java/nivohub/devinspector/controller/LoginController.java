package nivohub.devinspector.controller;

import javafx.concurrent.Task;
import nivohub.devinspector.exceptions.PasswordException;
import nivohub.devinspector.interactor.LoginInteractor;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.LoginViewBuilder;

public class LoginController extends BaseController{
    private final LoginInteractor interactor;
    private final ApplicationController applicationController;

    public LoginController(UserModel model, ApplicationController applicationController) {
        interactor = new LoginInteractor(model);
        viewBuilder = new LoginViewBuilder(model, this::loginUser);
        this.applicationController = applicationController;
    }

    private void loginUser() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws PasswordException {
                return interactor.attemptLogin();
            }
        };
        task.setOnSucceeded(e -> {if (Boolean.TRUE.equals(task.getValue())){
            interactor.updateFailedLogin(false);
            applicationController.loadMainView();
        }
        });
        task.setOnFailed(e -> {
            interactor.updateFailedLogin(true);
        });
        task.run();
    }
}
