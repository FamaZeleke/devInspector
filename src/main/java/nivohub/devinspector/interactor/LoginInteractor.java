package nivohub.devinspector.interactor;

import javafx.beans.binding.Bindings;
import nivohub.devinspector.exceptions.PasswordException;
import nivohub.devinspector.model.UserModel;

public class LoginInteractor {
    private final UserModel model;

    public LoginInteractor(UserModel model) {
        this.model = model;
        model.authenticatedProperty().bind(Bindings.createBooleanBinding(this::isPasswordValid, model.inputPasswordProperty()));
    }
    private boolean isPasswordValid(){
        return model.getPassword().equals(model.getInputPassword());
    }

    public boolean attemptLogin() throws PasswordException {
        if (isPasswordValid()){
            return true;
        } else {
            throw new PasswordException("Invalid password");
        }
    }

    public void updateFailedLogin(Boolean result){
        model.loginFailedProperty().set(result);
    }
}
