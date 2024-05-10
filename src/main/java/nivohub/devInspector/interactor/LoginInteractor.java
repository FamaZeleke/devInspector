package nivohub.devInspector.interactor;

import javafx.beans.binding.Bindings;
import nivohub.devInspector.model.UserModel;

public class LoginInteractor {
    private UserModel model;

    public LoginInteractor(UserModel model) {
        this.model = model;
        model.passwordValidProperty().bind(Bindings.createBooleanBinding(this::isPasswordValid, model.passwordProperty()));
    }
    private boolean isPasswordValid(){
        return model.getPassword().equals(model.getValidPassword());
    }

    public boolean attemptLogin(){
        if (isPasswordValid()){
            model.loginFailedProperty().set(false);
            System.out.println("Password is valid!");
            return true;
        } else {
            model.loginFailedProperty().set(true);
            return false;
        }
    }
}
