package nivohub.devinspector;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nivohub.devinspector.controller.ApplicationController;
import nivohub.devinspector.controller.LoginController;
import nivohub.devinspector.interfaces.StageManager;
import nivohub.devinspector.model.UserModel;

public class AppRoot extends Application implements StageManager {

    private Stage primaryStage;
    private ApplicationController applicationController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        UserModel userModel = new UserModel();
        String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name").toLowerCase();
        String platform = osName.contains("mac") ? "mac" : "windows";
        userModel.setPlatform(platform);
        userModel.setOsArch(osArch);
        // Dependency Injection via compositon - Guice Might be a good idea
        this.applicationController = new ApplicationController(userModel, this);
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        LoginController loginController = new LoginController(userModel, applicationController);
        primaryStage.setScene(new Scene(loginController.getView()));
        primaryStage.show();
    }

    @Override
    public void switchView() {
        primaryStage.setScene(new Scene(applicationController.getView()));
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
    }


}