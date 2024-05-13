package nivohub.devinspector.controller;

import javafx.scene.control.MenuBar;
import javafx.util.Builder;
import nivohub.devinspector.interactor.MenuBarInteractor;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.model.View;
import nivohub.devinspector.view.MenuBarBuilder;


public class MenuBarController {
    private final Builder<MenuBar> viewBuilder;
    private final MenuBarInteractor interactor;
    private final ApplicationModel model;

    public MenuBarController(ApplicationModel applicationModel) {
        this.model = applicationModel;
        interactor = new MenuBarInteractor(model);
        viewBuilder = new MenuBarBuilder(this::handleView);
    }

    private void handleView(View view) {
        switch (view) {
            case HOME, DOCKER, CLI -> {
                interactor.showView(view);
            }
            case EXIT -> {
                System.exit(0);
            }
        }
    }

    public MenuBar getMenu(){
        return viewBuilder.build();
    }
}
