package nivohub.devinspector.controller;

import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.util.Builder;
import nivohub.devinspector.interactor.MenuBarInteractor;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.view.MenuBarBuilder;


public class MenuBarController {
    private final Builder<MenuBar> viewBuilder;
    private final MenuBarInteractor interactor;
    private final ApplicationModel model;

    public MenuBarController(ApplicationModel applicationModel) {
        this.model = applicationModel;
        interactor = new MenuBarInteractor(model);
        viewBuilder = new MenuBarBuilder(this::handleNavigation);
    }

    private void handleNavigation(String viewName) {
        switch (viewName){
            case "Home":
                interactor.showHome();
                break;
            default:
                throw new IllegalArgumentException("Unknown View");
        }

    }

    public MenuBar getMenu(){
        return viewBuilder.build();
    }
}
