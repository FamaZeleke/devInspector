package nivohub.devinspector.interactor;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import javafx.application.Platform;
import nivohub.devinspector.docker.DockerContainerObject;
import nivohub.devinspector.docker.DockerEngine;
import nivohub.devinspector.docker.DockerImageObject;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        stopLogStream(null);
        String result;
        result = dockerEngine.pullAndRunContainer(model.selectedImageProperty().get(), model.selectedTagProperty().get(), model.formContainerNameProperty().get(), Integer.parseInt(model.formContainerHostPortProperty().get()), Integer.parseInt(model.formContainerPortProperty().get()));
        InspectContainerResponse containerInfo = dockerEngine.getContainerInfo(result);
        DockerContainerObject containerObject = createContainerObject(containerInfo, result);
        addContainerToList(containerObject);
        streamLogs(result);
        return result;
    }

    public String buildAndRunContainerFromDockerfile() throws InterruptedException, BindingPortAlreadyAllocatedException {
        stopLogStream(null);
        String result;
        result = dockerEngine.buildAndRunContainerFromDockerfile(model.dockerFileProperty().get(), model.formContainerNameProperty().get(), Integer.parseInt(model.formContainerHostPortProperty().get()), Integer.parseInt(model.formContainerPortProperty().get()));
        InspectContainerResponse containerInfo = dockerEngine.getContainerInfo(result);
        DockerContainerObject containerObject = createContainerObject(containerInfo, result);
        addContainerToList(containerObject);
        streamLogs(result);
        return result;
    }

    public void removeContainer(String containerId) {
        stopLogStream(containerId);
        dockerEngine.removeContainer(containerId);
    }

    public void startContainer(String containerId) throws BindingPortAlreadyAllocatedException {
        stopLogStream(null);
        dockerEngine.startContainer(containerId);
    }

    public void stopContainer(String containerId) {
        stopLogStream(containerId);
        dockerEngine.stopContainer(containerId);
    }

    public void openBrowserToPort(String containerId) throws IOException, URISyntaxException {
        String ports = getPortsForContainer(containerId);
        String url = "http://localhost:" + ports;
        java.awt.Desktop.getDesktop().browse(new URI(url));
    }

    public void streamLogs(String containerId) {
        addToOutput("Starting log stream...");
        model.updateContainerListeningStatus(containerId, true);
        logThread = new Thread(() -> dockerEngine.streamContainerLogs(containerId, logMessage ->
                Platform.runLater(() -> model.addToOutput(logMessage))));
        logThread.start();
    }

    public void stopLogStream(String containerId) {
        if (containerId != null && logThread != null) {
            logThread.interrupt();
            model.updateContainerListeningStatus(containerId, false);
            addToOutput("Log stream stopped");
        } else {
            addToOutput("No log stream to stop");
        }
    }

    //Model operations
    public void addToOutput(String message) {
        model.addToOutput(" \n" + message);
    }

    public void addContainerToList(DockerContainerObject container) {
        model.addContainerToList(container);
    }

    public void removeContainerFromList(String containerId) {
        model.removeContainerFromList(containerId);
    }

    public void updateContainerStatus(String containerId, Boolean status) {
        model.updateContainerStatus(containerId, status);
    }

    private String getPortsForContainer(String containerId) {
        return this.model.getDockerContainers().stream()
                .filter(container -> container.getContainerId().equals(containerId))
                .findFirst()
                .map(DockerContainerObject::getHostPort)
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
    public DockerContainerObject createContainerObject(InspectContainerResponse containerInfo, String containerId) {
        String containerName = containerInfo.getName().substring(1); // Remove leading slash
        Map.Entry<ExposedPort, Ports.Binding[]> portBindings = containerInfo.getHostConfig().getPortBindings().getBindings().entrySet().iterator().next(); // Get first port binding
        String hostPort = String.valueOf(portBindings.getValue()[0].getHostPortSpec()); // Get host port
        String exposedPort = String.valueOf(portBindings.getKey().getPort());
        String imageName = containerInfo.getConfig().getImage();
        Boolean status = containerInfo.getState().getRunning();
        return new DockerContainerObject(containerId, containerName, hostPort, exposedPort, imageName, status);
    }

    public DockerImageObject createImageObject(InspectImageResponse imageInfo) {
        String imageId = imageInfo.getId();
        List<String> repoTags = imageInfo.getRepoTags();
        String imageName = "";
        if (repoTags != null && !repoTags.isEmpty()) {
            imageName = repoTags.get(0).split(":")[0];
        }
        String[] tags = imageInfo.getRepoTags().stream().map(tag -> tag.split(":")[1]).toArray(String[]::new);
        String architecture = imageInfo.getArch();
        String os = imageInfo.getOs();
        String container = imageInfo.getContainer();
        return new DockerImageObject(imageId, imageName, tags, architecture, os, container);
    }

    public void listContainers() {
        List<Container> containers = dockerEngine.listContainers();
        containers.forEach(container -> {
            InspectContainerResponse containerInfo = dockerEngine.getContainerInfo(container.getId());
            DockerContainerObject containerObject = createContainerObject(containerInfo, container.getId());
            addContainerToList(containerObject);
        });
    }

    public void listImages() {
        dockerEngine.listImages().forEach(image -> {
            InspectImageResponse imageInfo = dockerEngine.getImageInfo(image.getId());
            DockerImageObject imageObject = createImageObject(imageInfo);
            model.addDockerImageTags(imageObject);
        });
    }
}
