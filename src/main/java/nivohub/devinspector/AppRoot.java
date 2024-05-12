package nivohub.devinspector;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nivohub.devinspector.controller.ApplicationController;
import nivohub.devinspector.controller.LoginController;
import nivohub.devinspector.model.UserModel;

public class AppRoot extends Application implements StageManager {

    private Stage primaryStage;
    private ApplicationController applicationController;
    private UserModel userModel;

    public static void main(String[] args) {
        launch(args);


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.userModel = new UserModel();
        this.applicationController = new ApplicationController(userModel, this);
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        LoginController loginController = new LoginController(userModel, applicationController);
        primaryStage.setScene(new Scene(loginController.getView()));
        primaryStage.show();
    }

    @Override
    public void switchScene() {
        primaryStage.setScene(new Scene(applicationController.getView()));
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
    }


//    @Override
//    public void start(Stage primaryStage) {
//
//        String osArch = System.getProperty("os.arch");
//        String osName = System.getProperty("os.name").toLowerCase();
//        String platform = osName.contains("mac") ? "mac" : "windows";
//
//
//        SceneController sceneController = new SceneController(primaryStage);
//        CommandLineController commandLineController = new CommandLineController(platform);
//
//        LoginScene loginScene = new LoginScene();
//        User user = new User(osArch, platform);
//        LoginController loginController = new LoginController(loginScene, user, sceneController, commandLineController);
//        loginScene.setController(loginController);
//        loginScene.setSubmitAction();
//
//        primaryStage.setScene(loginScene.createScene());
//        primaryStage.setTitle("DevInspector");
//        primaryStage.show();
//
//        Platform.runLater(primaryStage::requestFocus);
//    }

}