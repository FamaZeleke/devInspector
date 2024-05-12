package nivohub.devinspector.controller;

import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import nivohub.devinspector.exceptions.DockerNotRunningException;
import nivohub.devinspector.interactor.DockerInteractor;
import nivohub.devinspector.model.DockerModel;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.DockerViewBuilder;

public class DockerController {

    private final DockerInteractor interactor;
    private final Builder<Region> viewBuilder;

    public DockerController(UserModel userModel) {
        DockerModel model = new DockerModel();
        interactor = new DockerInteractor(model, userModel);
        viewBuilder = new DockerViewBuilder(model, this::startContainerAction, this::connectDocker);
    }

    private void connectDocker() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws DockerNotRunningException {
                interactor.connectDocker();
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            interactor.updateModelConnection();
        });
        task.setOnFailed(e -> {
            interactor.addToOutput(e.getSource().getException().getMessage());
    });
        new Thread(task).start();
    }

    private void startContainerAction() {

    }

    public Region getView(){
        return viewBuilder.build();
    }
}
