package nivohub.devinspector.controller;

import javafx.concurrent.Task;
import nivohub.devinspector.exceptions.BindingPortAlreadyAllocatedException;
import nivohub.devinspector.exceptions.DockerNotRunningException;
import nivohub.devinspector.interactor.DockerInteractor;
import nivohub.devinspector.interfaces.DockerInterface;
import nivohub.devinspector.model.DockerModel;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.DockerViewBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DockerController extends BaseController implements DockerInterface {
    private final DockerInteractor interactor;

    public DockerController(UserModel userModel) {
        DockerModel model = new DockerModel();
        interactor = new DockerInteractor(model, userModel);
        viewBuilder = new DockerViewBuilder(model, this::pullAndRunContainer, this::connectDocker, this::disconnectDocker, this::openBrowserToContainerBindings, this::uploadFileEvent, this::exportFileAction, this::startContainer, this::stopContainer, this::removeContainer);
    }

    private void exportFileAction() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws FileNotFoundException {
                interactor.exportFile();
                return null;
            }
        };
        task.setOnSucceeded(e -> interactor.addToOutput("File exported"));
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        // Running as Task as this is unlikely to be a long-running task
        task.run();
    }

    private void uploadFileEvent(File file) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws IOException {
                return interactor.uploadDockerFile(file);
            }
        };
        task.setOnSucceeded(e -> interactor.addToOutput("File uploaded: "+e.getSource().getValue()));
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        task.run();
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

    @Override
    public void connectDocker() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws DockerNotRunningException {
                interactor.connectDocker();
                return null;
            }
        };
        task.setOnSucceeded(e -> interactor.updateModelConnection(true));
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    @Override
    public void disconnectDocker() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws IOException {
                interactor.disconnectDocker();
                return null;
            }
        };
        task.setOnSucceeded(e -> interactor.updateModelConnection(false));
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        task.run();
    }

    private void startContainer(String containerId) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws BindingPortAlreadyAllocatedException {
                interactor.startContainer(containerId);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            interactor.addToOutput("Container started");
            interactor.updateContainerStatus(containerId, true);
        });
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        task.run();
    }

    private void stopContainer(String containerId) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                interactor.stopContainer(containerId);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            interactor.addToOutput("Container stopped");
            interactor.updateContainerStatus(containerId, false);
        });
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        task.run();
    }

    private void removeContainer(String containerId) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                interactor.removeContainer(containerId);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
                    interactor.addToOutput("Container removed");
                    interactor.removeContainerFromList(containerId);
                });
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        task.run();
    }

    private void pullAndRunContainer() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws InterruptedException, BindingPortAlreadyAllocatedException {
                return interactor.pullAndRunContainer();
            }
        };
        task.setOnSucceeded(e -> interactor.addToOutput("Container created with id: " + e.getSource().getValue()));
        task.setOnFailed(e -> interactor.addToOutput(e.getSource().getException().getMessage()));
        new Thread(task).start();
    }
}
