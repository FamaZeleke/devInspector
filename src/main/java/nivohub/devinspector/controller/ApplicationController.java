package nivohub.devinspector.controller;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import nivohub.devinspector.StageManager;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.ApplicationViewBuilder;

public class ApplicationController {
    private final Builder<Region> viewBuilder;
    private final UserModel userModel;
    private final StageManager stageManager;

    public ApplicationController(UserModel userModel, StageManager stageManager) {
        this.stageManager = stageManager;
        ApplicationModel appModel = new ApplicationModel();
        this.userModel = userModel;
        viewBuilder = new ApplicationViewBuilder(appModel, userModel,
                new MenuBarController(appModel).getMenu(),
                new HomeController().getView());
    }

    public Region getView(){
        return viewBuilder.build();
    }

    public void loadMainView() {
        stageManager.switchScene();
    }
}
