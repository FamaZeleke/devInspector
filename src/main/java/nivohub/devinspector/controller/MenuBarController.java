package nivohub.devinspector.controller;

import javafx.scene.control.MenuBar;
import javafx.util.Builder;
import nivohub.devinspector.interactor.MenuBarInteractor;
import nivohub.devinspector.interfaces.DockerInterface;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.enums.View;
import nivohub.devinspector.view.MenuBarBuilder;


public class MenuBarController {
    private final Builder<MenuBar> viewBuilder;
    private final MenuBarInteractor interactor;

    public MenuBarController(ApplicationModel applicationModel, DockerInterface dockerInterface) {
        interactor = new MenuBarInteractor(applicationModel);
        viewBuilder = new MenuBarBuilder(this::handleView, dockerInterface);
    }

    private void handleView(View view) {
        switch (view) {
            case HOME, DOCKER, CLI -> interactor.showView(view);
            case EXIT -> System.exit(0);
        }
    }

    public MenuBar getView(){
        return viewBuilder.build();
    }
}
