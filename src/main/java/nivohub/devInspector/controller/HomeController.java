package nivohub.devInspector.controller;

import nivohub.devInspector.model.DockerManager;
import nivohub.devInspector.view.HomeScene;

public class HomeController {
    private final HomeScene view;
    public HomeController(HomeScene view, DockerManager dockerManager) {
        this.view = view;
    }


}
