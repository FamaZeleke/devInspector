package nivohub.devinspector.controller;

import nivohub.devinspector.interfaces.StageManager;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.ApplicationBuilder;

public class ApplicationController extends BaseController {
    private final StageManager stageManager;

    public ApplicationController(UserModel userModel, StageManager stageManager) {
        this.stageManager = stageManager;
        DockerController dockerController = new DockerController(userModel);
        ApplicationModel appModel = new ApplicationModel();
        viewBuilder = new ApplicationBuilder(appModel,
                new MenuBarController(appModel, dockerController).getView(),
                new HomeController().getView(),
                new CLController(userModel).getView(),
                dockerController.getView());
    }


    public void loadMainView() {
        stageManager.switchView();
    }
}
