package nivohub.devinspector.docker;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

//POJO for Docker Container
public class DockerContainerObject {
    private final String containerId;
    private final String containerName;
    private final String hostPort;
    private final String exposedPort;
    private final String image;
    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private final BooleanProperty listening = new SimpleBooleanProperty(false);

    public DockerContainerObject(String containerId, String containerName, String hostPort, String exposedPort, String image, Boolean status) {
        this.containerId = containerId;
        this.containerName = containerName;
        this.hostPort = hostPort;
        this.exposedPort = exposedPort;
        this.image = image;
        running.set(status);
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

    public String getExposedPort() {
        return exposedPort;
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

}