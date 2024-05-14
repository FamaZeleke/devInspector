package nivohub.devinspector.controller;

import nivohub.devinspector.interfaces.DockerInterface;
import nivohub.devinspector.model.DockerModel;
import nivohub.devinspector.view.HomeViewBuilder;

public class HomeController extends BaseController{
    public HomeController(DockerModel dockerModel, DockerInterface dockerInterface) {
        viewBuilder = new HomeViewBuilder(dockerModel, dockerInterface);
    }

}
