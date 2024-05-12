package nivohub.devinspector.controller;

import javafx.scene.layout.Region;
import javafx.util.Builder;
import nivohub.devinspector.StageManager;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.ApplicationBuilder;

public class ApplicationController {
    private final Builder<Region> viewBuilder;
    private final StageManager stageManager;

    public ApplicationController(UserModel userModel, StageManager stageManager) {
        this.stageManager = stageManager;
        ApplicationModel appModel = new ApplicationModel();
        viewBuilder = new ApplicationBuilder(appModel,
                new MenuBarController(appModel).getMenu(),
                new HomeController().getView(),
                new CLController(userModel).getView(),
                new DockerController().getView()
        );
    }

    public Region getView(){
        return viewBuilder.build();
    }

    public void loadMainView() {
        stageManager.switchScene();
    }
}
