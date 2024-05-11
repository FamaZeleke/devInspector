package nivohub.devinspector.controller;

import javafx.scene.layout.Region;
import javafx.util.Builder;
//import nivohub.devinspector.model.DockerManager;
//import nivohub.devinspector.view.HomeScene;
import nivohub.devinspector.view.HomeViewBuilder;

public class HomeController {
    private final Builder<Region> viewBuilder;

    public HomeController() {
        this.viewBuilder = new HomeViewBuilder();
    }

    public Region getView() {
        return viewBuilder.build();
    }


}
