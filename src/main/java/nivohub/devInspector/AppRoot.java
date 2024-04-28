package nivohub.devInspector;

import javafx.application.Application;
import javafx.stage.Stage;
import nivohub.devInspector.controller.LoginController;
import nivohub.devInspector.controller.SceneController;
import nivohub.devInspector.model.User;
import nivohub.devInspector.view.LoginScene;

public class AppRoot extends Application {

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

        SceneController sceneController = new SceneController(primaryStage);

        LoginScene loginScene = new LoginScene();
        User user = new User();
        LoginController loginController = new LoginController(loginScene, user, sceneController);
        loginScene.setController(loginController);
        loginScene.setSubmitAction(e -> loginController.handleSubmit());

        primaryStage.setScene(loginScene.createScene());
        primaryStage.setTitle("Nivo Debugger Wizard");
        primaryStage.show();
    }

}