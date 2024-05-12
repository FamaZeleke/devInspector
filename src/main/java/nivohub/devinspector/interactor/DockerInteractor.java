package nivohub.devinspector.interactor;

import javafx.application.Platform;
import nivohub.devinspector.docker.DockerContainer;
import nivohub.devinspector.docker.DockerEngine;
import nivohub.devinspector.exceptions.DockerNotRunningException;
import nivohub.devinspector.model.DockerModel;
import nivohub.devinspector.model.UserModel;

public class DockerInteractor {
    private final DockerModel model;
    private final DockerEngine dockerEngine;
    private Thread logThread;

    public DockerInteractor(DockerModel model, UserModel userModel) {
        this.model = model;
        this.dockerEngine = new DockerEngine(userModel.getPlatform());
    }

    public void connectDocker() throws DockerNotRunningException {
        try {
            dockerEngine.createDockerClient();
        } catch (Exception e) {
            throw new DockerNotRunningException(e.getMessage());
        }
    }

    public void updateModelConnection() {
        model.dockerConnectedProperty().set(true);
    }

    public String pullAndRunContainer() {
        stopLogStream();
        DockerContainer result = null;
        try {
            result = dockerEngine.createAndRunContainer(model.selectedImageProperty().get(), model.selectedTagProperty().get(), model.formContainerNameProperty().get(), Integer.parseInt(model.formContainerHostPortProperty().get()), Integer.parseInt(model.formContainerPortProperty().get()));
        } catch (Exception e) {
            addToOutput(e.getMessage());
        }
        System.out.println("Container created with id: " + result.containerId());
        streamLogs(result.containerId());
        addContainerToList(result);
        return result.containerId();
    }

    public void addToOutput (String message) {
        model.addToOutput(message);
    }

    public void addContainerToList(DockerContainer container) {
        model.addContainerToList(container);
    }

    public void streamLogs(String containerId) {
        addToOutput("Starting log stream");
        logThread = new Thread(() -> {
            dockerEngine.streamContainerLogs(containerId, logMessage -> {
                Platform.runLater(() -> {
                    model.addToOutput(logMessage);
                });
            });
        });
        logThread.start();
    }

    public void stopLogStream() {
        if (logThread != null) {
            logThread.interrupt();
            addToOutput("Log stream stopped");
        }
    }




}
