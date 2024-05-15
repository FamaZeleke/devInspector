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
    private Thread thread;

    public DockerController(UserModel userModel, DockerModel model) {
        interactor = new DockerInteractor(model, userModel);
        viewBuilder = new DockerViewBuilder(model,
                this::pullAndRunContainer,
                this::buildAndRunContainerFromDockerfile,
                this::openBrowserToContainerBindings,
                this::uploadFileEvent,
                this::exportFileAction,
                this::saveFileAction,
                this::startContainer,
                this::stopContainer,
                this::removeContainer,
                this::listenToContainerLogs);
    }

    private void saveFileAction() {
        interactor.addToOutput("Saving file...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws IOException {
                interactor.saveFile();
                return null;
            }
        };
        task.setOnSucceeded(e -> interactor.addToOutput("File saved!"));
        task.setOnFailed(e -> interactor.addToOutput("Failed to save file: "+e.getSource().getException().getMessage()));
        // Running as Task as this is unlikely to be a long-running task
        task.run();
    }

    private void exportFileAction() {
        interactor.addToOutput("Exporting file...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws FileNotFoundException {
                interactor.exportFile();
                return null;
            }
        };
        task.setOnSucceeded(e -> interactor.addToOutput("File exported!"));
        task.setOnFailed(e -> interactor.addToOutput("Failed to export file: "+e.getSource().getException().getMessage()));
        // Running as Task as this is unlikely to be a long-running task
        task.run();
    }

    private void uploadFileEvent(File file) {
        interactor.addToOutput("Uploading file...");
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws IOException {
                return interactor.uploadDockerFile(file);
            }
        };
        task.setOnSucceeded(e -> interactor.addToOutput("File uploaded: "+e.getSource().getValue()));
        task.setOnFailed(e -> interactor.addToOutput("Failed to upload file: "+e.getSource().getException().getMessage()));
        task.run();
    }

    private void openBrowserToContainerBindings(String containerId) {
        interactor.addToOutput("Opening browser to container bindings...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                interactor.openBrowserToPort(containerId);
                return null;
            }
        };
        task.setOnSucceeded(e -> interactor.addToOutput("Browser opened to port: " + e.getSource().getValue()));
        task.setOnFailed(e -> interactor.addToOutput("Failed to open browser to port: "+e.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    private void listenToContainerLogs(String containerId) {
        interactor.addToOutput("Starting log stream...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                interactor.stopLogStream(containerId);
                interactor.streamLogs(containerId);
                return null;
            }
        };
        task.setOnSucceeded(e -> interactor.addToOutput("Log stream started"));
        task.setOnFailed(e -> interactor.addToOutput("Failed to start log stream: "+e.getSource().getException().getMessage()));
        task.run();
    }

    private void pullAndRunContainer() {
        interactor.updateModelThreadBuilding(true);
        interactor.addToOutput("Pulling and running container...");
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws InterruptedException, BindingPortAlreadyAllocatedException {
                return interactor.pullAndRunContainer();
            }
        };
        task.setOnSucceeded(e -> {
            interactor.addToOutput("Container created with id: " + e.getSource().getValue());
            interactor.updateModelThreadBuilding(false);
        });
        task.setOnFailed(e -> {
            interactor.addToOutput("Failed to pull and run container: " + e.getSource().getException().getMessage());
            interactor.updateModelThreadBuilding(false);
        });
        thread = new Thread(task);
        thread.start();
    }

    private void buildAndRunContainerFromDockerfile() {
        interactor.updateModelThreadBuilding(true);
        interactor.addToOutput("Building and running container from Dockerfile...");
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws BindingPortAlreadyAllocatedException {
                return interactor.buildAndRunContainerFromDockerfile();
            }
        };
        task.setOnSucceeded(e -> {
            interactor.addToOutput("Container created with id: " + e.getSource().getValue());
            interactor.updateModelThreadBuilding(false);
        });
        task.setOnFailed(e -> {
            interactor.addToOutput("Failed to build and run container from Dockerfile: " + e.getSource().getException().getMessage());
            interactor.updateModelThreadBuilding(false);
        });
        thread = new Thread(task);
        thread.start();
    }

    @Override
    public void connectDocker() {
        interactor.addToOutput("Connecting to Docker...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws DockerNotRunningException {
                interactor.connectDocker();
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            interactor.addToOutput("Connected to Docker");
            interactor.updateModelConnection(true);
            interactor.listContainers();
            interactor.listImages();
        });
        task.setOnFailed(e -> interactor.addToOutput("Failed to connect to Docker :"+e.getSource().getException().getMessage()));
        new Thread(task).start();
    }

    @Override
    public void disconnectDocker() {
        interactor.addToOutput("Disconnecting from Docker...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws IOException {
                interactor.disconnectDocker();
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            interactor.addToOutput("Disconnected from Docker");
            interactor.updateModelConnection(false);
        });
        task.setOnFailed(e -> interactor.addToOutput("Failed to disconnect from Docker: "+e.getSource().getException().getMessage()));
        task.run();
    }

    @Override
    public void startContainer(String containerId) {
        interactor.addToOutput("Starting container...");
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
            interactor.streamLogs(containerId);
        });
        task.setOnFailed(e -> interactor.addToOutput("Failed to start container: "+e.getSource().getException().getMessage()));
        task.run();
    }

    @Override
    public void stopContainer(String containerId) {
        interactor.addToOutput("Stopping container...");
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
        task.setOnFailed(e -> interactor.addToOutput("Failed to stop container :"+e.getSource().getException().getMessage()));
        task.run();
    }

    @Override
    public void removeContainer(String containerId) {
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
        task.setOnFailed(e -> interactor.addToOutput("Failed to remove container: "+e.getSource().getException().getMessage()));
        task.run();
    }

    @Override
    public void stopThread() {
        thread.interrupt();
        interactor.addToOutput("Thread stopped");
        interactor.updateModelThreadBuilding(false);
    }
}
