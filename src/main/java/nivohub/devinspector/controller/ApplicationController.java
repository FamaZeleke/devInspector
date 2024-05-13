package nivohub.devinspector.controller;

import nivohub.devinspector.StageManager;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.ApplicationBuilder;

public class ApplicationController extends BaseController {
    private final StageManager stageManager;

    public ApplicationController(UserModel userModel, StageManager stageManager) {
        this.stageManager = stageManager;
        ApplicationModel appModel = new ApplicationModel();
        viewBuilder = new ApplicationBuilder(appModel,
                new MenuBarController(appModel).getView(),
                new HomeController().getView(),
                new CLController(userModel).getView(),
                new DockerController(userModel).getView()
        );
    }


    public void loadMainView() {
        stageManager.switchScene();
    }
}
