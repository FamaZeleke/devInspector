package nivohub.devInspector.view;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Builder;
import nivohub.devInspector.model.UserModel;

import java.util.function.Consumer;
import java.util.function.Supplier;


public class LoginViewBuilder implements Builder<Region> {

    private final UserModel model;
    private final Supplier<Boolean> loginHandler;

    public LoginViewBuilder(UserModel model, Supplier<Boolean> loginHandler) {
        this.model = model;
        this.loginHandler = loginHandler;
    }

    @Override
    public Region build() {
        GridPane results = new GridPane();
        results.setPadding(new Insets(25));

        results.setHgap(10);
        results.setVgap(10);
        results.setAlignment(Pos.CENTER);

        results.add(welcomeLabel(), 0, 0);
        results.add(nameLabel(), 0, 1);
        results.add(nameInput(), 1, 1);
        results.add(passwordLabel(), 0, 2);
        results.add(passwordInput(), 1, 2);
        results.add(createLoginButton(), 1, 3);
        results.add(errorMessage(), 1, 4);

        return results;
    }


    private Node createLoginButton() {
        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> {
            boolean loginSuccessful = loginHandler.get();
            if (!loginSuccessful) {
                System.out.println("Invalid password");
            }
        });
        return loginButton;
    }

    private Node passwordInput(){
        return boundTextField(model.passwordProperty(), "Enter Password");
    }

    private Node nameInput(){
        return boundTextField(model.fullNameProperty(), "Enter Full Name");
    }

    private Node welcomeLabel() {
        Label label = new Label("Welcome");
        label.setFont(Font.font("Inter", FontWeight.MEDIUM, 20));
        return label;
    }

    private Node errorMessage(){
        Label label = new Label();
        label.setTextFill(Color.RED);
        return label;
    }

    private Node passwordLabel(){
        return label("Password");
    }

    private Node nameLabel(){
        return label("Name");
    }

    private Node boundTextField(StringProperty boundProperty, String promptText){
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.textProperty().bindBidirectional(boundProperty);
        return textField;
    }

    private Node label(String contents){
        return new Label(contents);
    }
}
