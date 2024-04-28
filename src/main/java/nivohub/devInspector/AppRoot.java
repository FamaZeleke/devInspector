package nivohub.devInspector;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import nivohub.devInspector.controller.DockerController;
import nivohub.devInspector.controller.SceneController;
import nivohub.devInspector.exceptions.FullNameException;
import nivohub.devInspector.exceptions.PasswordException;
import nivohub.devInspector.model.DockerManager;
import nivohub.devInspector.view.AppMenu;
import nivohub.devInspector.view.DockerScene;
import nivohub.devInspector.view.Home;

public class AppRoot extends Application {
    private SceneController sceneController;
    private Stage primaryStage;
    private User user;
    private String platform;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        String osArch = System.getProperty("os.arch");
        String platform = "linux/amd64"; // default to amd64

        if (osArch.contains("arm")) {
            platform = "linux/arm64";
        }

        // Store the platform in a field for later use
        this.platform = platform;

        this.sceneController = new SceneController(primaryStage);
        this.primaryStage = primaryStage;
        this.showLoginScene();
        primaryStage.setTitle("Nivo Debugger Wizard");
        primaryStage.show();
    }

    private void showLoginScene() {
        Label welcomeLabel = new Label("Welcome");
        welcomeLabel.setFont(Font.font("Inter", FontWeight.MEDIUM, 20));

        Label nameLabel = new Label("Full Name");
        TextField nameInput = new TextField();

        Label passwordLabel = new Label("Password");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Enter password");

        Label errorMessage = new Label();
        Button submitButton = new Button("Submit");
        passwordInput.setOnAction(e -> submitButton.fire());
        submitButton.setOnAction(event -> {
            try {
                user = new User();
                user.setFullName(nameInput.getText());
                user.validatePassword(passwordInput.getText());

                // Create Home and DockerApplication instances after the user has logged in
                Home home = new Home(user, new AppMenu(sceneController));
                this.sceneController.addScene("Home", home);

                DockerManager dockerManager = new DockerManager();
                DockerScene dockerScene = new DockerScene(new AppMenu(sceneController));
                DockerController dockerController = new DockerController(dockerScene, dockerManager);
                dockerScene.setController(dockerController);
                this.sceneController.addScene("Docker", dockerScene);

                this.sceneController.showScene("Home");
            } catch (FullNameException exception) {
                errorMessage.setText(exception.getMessage()); // display error message
            } catch (PasswordException e) {
                errorMessage.setText(e.getMessage());
            } catch (DockerCertificateException e) {
                throw new RuntimeException(e);
            }
        });

        GridPane grid = new GridPane();
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


        Scene scene = new Scene(grid, 600, 300);
        this.primaryStage.setScene(scene);
    }

}