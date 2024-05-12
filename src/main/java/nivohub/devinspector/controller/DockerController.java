package nivohub.devinspector.controller;

import javafx.scene.layout.Region;
import javafx.util.Builder;
import nivohub.devinspector.interactor.DockerInteractor;
import nivohub.devinspector.model.DockerModel;
import nivohub.devinspector.view.DockerViewBuilder;

public class DockerController {

    private final DockerInteractor interactor;
    private final Builder<Region> viewBuilder;
    private final DockerModel model;

    public DockerController() {
        model = new DockerModel();
        interactor = new DockerInteractor(model);
        viewBuilder = new DockerViewBuilder(model);
    }

    public Region getView(){
        return viewBuilder.build();
    }
}
