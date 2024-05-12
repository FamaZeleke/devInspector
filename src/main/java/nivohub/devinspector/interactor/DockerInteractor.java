package nivohub.devinspector.interactor;

import javafx.application.Platform;
import nivohub.devinspector.docker.DockerEngine;
import nivohub.devinspector.exceptions.DockerNotRunningException;
import nivohub.devinspector.model.DockerModel;
import nivohub.devinspector.model.UserModel;

public class DockerInteractor {
    private final DockerModel model;
    private final DockerEngine dockerEngine;

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

    public void addToOutput (String message) {
        model.addToOutput(message);
    }

    public void streamLogs(String containerId) {
        new Thread(() -> {
            dockerEngine.streamContainerLogs(containerId, logMessage -> {
                Platform.runLater(() -> {
                    model.addToOutput(logMessage);
                });
            });
        }).start();
    }
}
