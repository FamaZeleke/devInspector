package nivohub.devinspector.interactor;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import javafx.application.Platform;
import nivohub.devinspector.docker.DockerContainer;
import nivohub.devinspector.docker.DockerEngine;
import nivohub.devinspector.exceptions.BindingPortAlreadyAllocatedException;
import nivohub.devinspector.exceptions.DockerNotRunningException;
import nivohub.devinspector.model.DockerModel;
import nivohub.devinspector.model.UserModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Map;

public class DockerInteractor {
    private final DockerModel model;
    private final DockerEngine dockerEngine;
    private Thread logThread;

    public DockerInteractor(DockerModel model, UserModel userModel) {
        this.model = model;
        this.dockerEngine = new DockerEngine(userModel.getPlatform());
    }

    // Connect to the Docker engine
    public void connectDocker() throws DockerNotRunningException {
        dockerEngine.createDockerClient();
    }

    public void disconnectDocker() throws IOException {
        dockerEngine.closeDockerClient();
    }

    public void updateModelConnection(Boolean status) {
        model.dockerConnectedProperty().set(status);
    }

    //Container operations
    public String pullAndRunContainer() throws InterruptedException, BindingPortAlreadyAllocatedException {
        stopLogStream();
        String result;
        result = dockerEngine.createAndRunContainer(model.selectedImageProperty().get(), model.selectedTagProperty().get(), model.formContainerNameProperty().get(), Integer.parseInt(model.formContainerHostPortProperty().get()), Integer.parseInt(model.formContainerPortProperty().get()));
        InspectContainerResponse containerInfo = dockerEngine.getContainerInfo(result);
        DockerContainer containerObject = setContainerObject(containerInfo, result);
        addContainerToList(containerObject);
        streamLogs(result);
        return result;
    }

    public void removeContainer(String containerId) {
            stopLogStream();
            dockerEngine.removeContainer(containerId);
    }

    public void startContainer(String containerId) throws BindingPortAlreadyAllocatedException {
        stopLogStream();
        dockerEngine.startContainer(containerId);
    }

    public void stopContainer(String containerId) {
        stopLogStream();
        dockerEngine.stopContainer(containerId);
    }

    public void openBrowserToPort(String containerId) throws IOException, URISyntaxException {
        String ports = getPortsForContainer(containerId);
        String url = "http://localhost:"+ports;
        java.awt.Desktop.getDesktop().browse(new URI(url));
    }

    public void streamLogs(String containerId) {
        addToOutput("Starting log stream");
        logThread = new Thread(() -> dockerEngine.streamContainerLogs(containerId, logMessage ->
                Platform.runLater(() -> model.addToOutput(logMessage))));
        logThread.start();
    }

    public void stopLogStream() {
        if (logThread != null) {
            logThread.interrupt();
            addToOutput("Log stream stopped");
        }
    }

    //Model operations
    public void addToOutput (String message) {
        model.addToOutput(" \n"+message);
    }

    public void addContainerToList(DockerContainer container) {
        model.addContainerToList(container);
    }

    public void removeContainerFromList(String containerId) {
        model.removeContainerFromList(containerId);
    }

    public void updateContainerStatus(String containerId, Boolean status) {
        model.updateContainerStatus(containerId, status);
    }

    private String getPortsForContainer(String containerId) {
        return this.model.getRunningContainers().stream()
                .filter(container -> container.getContainerId().equals(containerId))
                .findFirst()
                .map(DockerContainer::getHostPort)
                .map(Object::toString)
                .orElse(null);
    }

    public String uploadDockerFile(File file) throws IOException {
            String result = Files.readString(file.toPath());
            model.dockerFileTextProperty().set(result);
            model.dockerFileProperty().set(file);
            return file.getName();
    }

    public void exportFile() throws FileNotFoundException {
            PrintWriter writer = new PrintWriter(model.dockerFileProperty().get());
            writer.write(model.dockerFileTextProperty().get());
            writer.close();
    }

    //Helpers
    public DockerContainer setContainerObject(InspectContainerResponse containerInfo, String containerId) {
        String containerNameFromInfo = containerInfo.getName().substring(1); // Remove leading slash
        Map.Entry<ExposedPort,Ports.Binding[]> portBindings = containerInfo.getHostConfig().getPortBindings().getBindings().entrySet().iterator().next();
        String hostPortFromInfo = String.valueOf(portBindings.getValue()[0].getHostPortSpec());
        String exposedPortFromInfo = String.valueOf(portBindings.getKey().getPort());
        String imageFromInfo = containerInfo.getConfig().getImage();
        Boolean status = containerInfo.getState().getRunning();
        return new DockerContainer(containerId, containerNameFromInfo, hostPortFromInfo, exposedPortFromInfo, imageFromInfo, status);
    }
}
