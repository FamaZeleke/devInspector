package nivohub.devInspector.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import nivohub.devInspector.controller.LoginController;
import nivohub.devInspector.controller.SceneController;

public class LoginScene implements SceneController.SceneCreator {
    private Label welcomeLabel = new Label("Welcome");
    private Label nameLabel = new Label("Full Name");
    private TextField nameInput = new TextField();
    private Label passwordLabel = new Label("Password");
    private PasswordField passwordInput = new PasswordField();
    private Label errorMessage = new Label();
    private Button submitButton = new Button("Submit");
    private GridPane grid = new GridPane();
    private LoginController controller;

    public Scene createScene() {
        welcomeLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 20));
        passwordInput.setPromptText("Enter password");

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(welcomeLabel, 0, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameInput, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordInput, 1, 2);
        grid.add(errorMessage, 1, 4);
        grid.add(submitButton, 1, 3);

        return new Scene(grid, 600, 300);

    }

    public void setController(LoginController controller) {
        this.controller = controller;
    }

    public String getFullNameInput() {
        return nameInput.getText();
    }

    public String getPasswordInput() {
        return passwordInput.getText();
    }

    public void setErrorMessage(String message) {
        errorMessage.setText(message);
    }

    public void setSubmitAction(EventHandler<ActionEvent> action) {
    submitButton.setOnAction(action);
    }

}
