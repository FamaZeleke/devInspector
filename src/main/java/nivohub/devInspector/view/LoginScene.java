package nivohub.devInspector.view;

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

/**
 * The LoginScene class represents a scene for user login in the application.
 * It extends the BaseScene class and implements the SceneController.SceneCreator interface.
 * The scene contains UI elements such as labels, text fields, and buttons for user input and interaction.
 * The class provides methods to retrieve user input, set error messages, and handle submit actions.
 */
public class LoginScene extends Scene {
    private final Label welcomeLabel = new Label("Welcome");
    private final Label nameLabel = new Label("Full Name");
    private final TextField nameInput = new TextField();
    private final Label passwordLabel = new Label("Password");
    private final PasswordField passwordInput = new PasswordField();
    private final Label errorMessage = new Label();
    private final Button submitButton = new Button("Submit");
    private final GridPane grid = new GridPane();
    private LoginController controller;

    public LoginScene() {
        super(null);
    }

    public void setController(Object controller) {
        if (controller instanceof LoginController) {
            this.controller = (LoginController) controller;
        } else {
            throw new IllegalArgumentException("Controller must be a LoginController");
        }
    }

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

    public String getFullNameInput() {
        return nameInput.getText();
    }

    public String getPasswordInput() {
        return passwordInput.getText();
    }

    public void setErrorMessage(String message) {
        errorMessage.setText(message);
    }

    //Abstracting the business logic to the login controller
    public void setSubmitAction() {
        nameInput.setOnAction(e -> submitButton.fire());
        passwordInput.setOnAction(e -> submitButton.fire());
//        submitButton.setOnAction(e -> controller.handleSubmit());
    }

}
