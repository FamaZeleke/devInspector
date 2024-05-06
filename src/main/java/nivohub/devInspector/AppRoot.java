package nivohub.devInspector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import nivohub.devInspector.controller.CommandLineController;
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
        String osName = System.getProperty("os.name").toLowerCase();
        String platform = osName.contains("mac") ? "mac" : "windows";


        SceneController sceneController = new SceneController(primaryStage);
        CommandLineController commandLineController = new CommandLineController(platform);

        LoginScene loginScene = new LoginScene();
        User user = new User(osArch, platform);
        LoginController loginController = new LoginController(loginScene, user, sceneController, commandLineController);
        loginScene.setController(loginController);
        loginScene.setSubmitAction();

        primaryStage.setScene(loginScene.createScene());
        primaryStage.setTitle("DevInspector");
        primaryStage.show();
        
        Platform.runLater(primaryStage::requestFocus);
    }

}