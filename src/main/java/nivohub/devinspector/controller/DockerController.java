package nivohub.devinspector.controller;

import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import nivohub.devinspector.exceptions.DockerNotRunningException;
import nivohub.devinspector.interactor.DockerInteractor;
import nivohub.devinspector.model.DockerModel;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.DockerViewBuilder;

import java.io.File;

public class DockerController extends BaseController {
    private final DockerInteractor interactor;

    public DockerController(UserModel userModel) {
        DockerModel model = new DockerModel();
        interactor = new DockerInteractor(model, userModel);
        viewBuilder = new DockerViewBuilder(model, this::pullAndRunContainer, this::connectDocker, this::openBrowserToContainerBindings, this::uploadFileEvent, this::exportFileAction);
    }

    private void exportFileAction() {
    }

    private void uploadFileEvent(File file) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return interactor.uploadDockerFile(file);
            }
        };
        task.setOnSucceeded(e -> interactor.addToOutput("File uploaded: "+e.getSource().getValue()));
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    private void openBrowserToContainerBindings(String containerId) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                interactor.openBrowserToPort(containerId);
                return null;
            }
        };
        task.setOnSucceeded(e -> interactor.addToOutput("Browser opened to port: " + e.getSource().getValue()));
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    private void connectDocker() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws DockerNotRunningException {
                interactor.connectDocker();
                return null;
            }
        };
        task.setOnSucceeded(e -> interactor.updateModelConnection());
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    private void pullAndRunContainer() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return interactor.pullAndRunContainer();
            }
        };
        //TODO Validate that this thread exits
        task.setOnSucceeded(e -> interactor.addToOutput("Container created with id: " + e.getSource().getValue()));
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        new Thread(task).start();
    }

}
