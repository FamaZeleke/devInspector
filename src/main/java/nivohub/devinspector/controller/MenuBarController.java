package nivohub.devinspector.controller;

import nivohub.devinspector.interactor.MenuBarInteractor;
import nivohub.devinspector.interfaces.ApplicationInterface;
import nivohub.devinspector.interfaces.DockerInterface;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.enums.View;
import nivohub.devinspector.model.DockerModel;
import nivohub.devinspector.view.MenuBarBuilder;


public class MenuBarController extends BaseController{
    private final MenuBarInteractor interactor;

    public MenuBarController(ApplicationModel applicationModel, DockerInterface dockerInterface, DockerModel dockerModel, ApplicationInterface applicationInterface) {
        interactor = new MenuBarInteractor(applicationModel);
        viewBuilder = new MenuBarBuilder(this::handleView, dockerInterface, dockerModel, applicationInterface);
    }

    private void handleView(View view) {
        if (view == View.HOME || view == View.DOCKER || view == View.CLI) {
            interactor.showView(view);
        } else if (view == View.EXIT) {
            System.exit(0);
        }
    }
}
