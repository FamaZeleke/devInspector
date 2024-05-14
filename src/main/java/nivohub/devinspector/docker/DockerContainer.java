package nivohub.devinspector.docker;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class DockerContainer {
    private final String containerId;
    private final String containerName;
    private final String hostPort;
    private final String exposedPort;
    private final String image;
    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private final BooleanProperty listening = new SimpleBooleanProperty(false);

    public DockerContainer(String containerId, String containerName, String hostPort, String exposedPort, String image, Boolean status) {
        this.containerId = containerId;
        this.containerName = containerName;
        this.hostPort = hostPort;
        this.exposedPort = exposedPort;
        this.image = image;
        setRunning(status);
    }

    public String getContainerId() {
        return containerId;
    }

    public String getContainerName() {
        return containerName;
    }

    public String getHostPort() {
        return hostPort;
    }

    public String getImage() {
        return image;
    }

    public BooleanProperty getRunning() {
        return running;
    }

    public BooleanProperty runningProperty() {
        return running;
    }

    public BooleanProperty listeningProperty() {
        return listening;
    }
    public void setRunning(Boolean running) {
        this.running.set(running);
    }

    public void setListening(Boolean listening) {
        this.listening.set(listening);
    }

}