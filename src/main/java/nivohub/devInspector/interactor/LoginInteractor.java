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

    public boolean loginUser(){
        System.out.println("User Logged in!");
        return isPasswordValid();
    }

}
