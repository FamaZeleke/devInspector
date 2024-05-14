package nivohub.devinspector.interactor;

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

public class DockerInteractor {
    private final DockerModel model;
    private final DockerEngine dockerEngine;
    private Thread logThread;

    public DockerInteractor(DockerModel model, UserModel userModel) {
        this.model = model;
        this.dockerEngine = new DockerEngine(userModel.getPlatform());
    }

    public void connectDocker() throws DockerNotRunningException {
        dockerEngine.createDockerClient();
    }

    public void disconnectDocker() throws IOException {
        dockerEngine.closeDockerClient();
    }

    public void updateModelConnection(Boolean status) {
        model.dockerConnectedProperty().set(status);
    }

    public String pullAndRunContainer() throws InterruptedException, BindingPortAlreadyAllocatedException {
        stopLogStream();
        DockerContainer result;
        result = dockerEngine.createAndRunContainer(model.selectedImageProperty().get(), model.selectedTagProperty().get(), model.formContainerNameProperty().get(), Integer.parseInt(model.formContainerHostPortProperty().get()), Integer.parseInt(model.formContainerPortProperty().get()));
        streamLogs(result.getContainerId());
        addContainerToList(result);
        return result.getContainerId();
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


    public void openBrowserToPort(String containerId) throws IOException, URISyntaxException {
        String ports = getPortsForContainer(containerId);
        String url = "http://localhost:"+ports;
        java.awt.Desktop.getDesktop().browse(new URI(url));
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
}
