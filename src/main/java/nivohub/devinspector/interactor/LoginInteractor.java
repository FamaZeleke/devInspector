package nivohub.devinspector.interactor;

import javafx.beans.binding.Bindings;
import nivohub.devinspector.model.UserModel;

public class LoginInteractor {
    private UserModel model;

    public LoginInteractor(UserModel model) {
        this.model = model;
        model.authenticatedProperty().bind(Bindings.createBooleanBinding(this::isPasswordValid, model.inputPasswordProperty()));
    }
    private boolean isPasswordValid(){
        return model.getPassword().equals(model.getInputPassword());
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
