package nivohub.devinspector.controller;

import nivohub.devinspector.interfaces.ApplicationInterface;
import nivohub.devinspector.interfaces.StageManager;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.model.DockerModel;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.ApplicationBuilder;

public class ApplicationController extends BaseController implements ApplicationInterface {
    private final StageManager stageManager;

    public ApplicationController(UserModel userModel, StageManager stageManager) {
        this.stageManager = stageManager;
        DockerModel dockerModel = new DockerModel();
        DockerController dockerController = new DockerController(userModel, dockerModel);
        ApplicationModel appModel = new ApplicationModel();
        viewBuilder = new ApplicationBuilder(appModel,
                new MenuBarController(appModel, dockerController, dockerModel, this).getView(),
                new HomeController(dockerModel, dockerController).getView(),
                new CLController(userModel).getView(),
                dockerController.getView());
    }

    @Override
    public void loadMainView() {
        stageManager.switchView();
    }

    @Override
    public void exitApplication() {
        System.exit(0);
    }

    @Override
    public void toggleTheme() {
        stageManager.toggleTheme();
    }
}
