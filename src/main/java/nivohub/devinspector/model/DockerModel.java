package nivohub.devinspector.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nivohub.devinspector.docker.DockerContainerObject;
import nivohub.devinspector.docker.DockerImageObject;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DockerModel {

    private final ObservableList<DockerImageObject> dockerImages;
    private final ObservableList<DockerContainerObject> runningContainers = FXCollections.observableArrayList();
    private final ListProperty<String> selectedImageTags = new SimpleListProperty<>();
    private final ObjectProperty<File> dockerFile = new SimpleObjectProperty<>();
    private final StringProperty dockerFileText = new SimpleStringProperty();
    private final StringProperty output = new SimpleStringProperty("");
    private final StringProperty currentContainerId = new SimpleStringProperty();
    private final StringProperty selectedImage = new SimpleStringProperty();
    private final StringProperty selectedTag = new SimpleStringProperty();
    private final StringProperty formContainerName = new SimpleStringProperty("");
    private final StringProperty formContainerPort = new SimpleStringProperty("");
    private final StringProperty formContainerHostPort = new SimpleStringProperty("");
    private final BooleanBinding imageSelected;
    private final BooleanProperty dockerConnected = new SimpleBooleanProperty(false);

    public DockerModel() {
        this.dockerImages = FXCollections.observableArrayList(
                new DockerImageObject("kale5/rickroll", new String[]{"latest","arm64"}),
                new DockerImageObject("jupyter/base-notebook", new String[]{"latest", "arm64"}));

        // Create a binding to check if an image is selected - ui state management
        imageSelected = selectedImage.isNotNull();

        // Update the selectedImageTags list when the selectedImage changes - react to ui state changes
        selectedImage.addListener((observable, oldValue, newValue) -> updateSelectedImageTags(newValue));
    }

    public void updateSelectedImageTags(String newValue) {
        String[] tags = dockerImages.stream()
                .filter(dockerImage -> dockerImage.imageName().equals(newValue))
                .findFirst()
                .map(DockerImageObject::tags)
                .orElse(new String[0]);
        selectedImageTags.set(FXCollections.observableArrayList(tags));
    }

    public void addToOutput(String newOutput) {
        if (newOutput != null) {
            String currentOutput = this.output.get();
            this.output.setValue(currentOutput+newOutput);
        }
    }

    public ObservableList<String> getDockerImageNames() {
        return dockerImages.stream()
                .map(DockerImageObject::imageName)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<DockerContainerObject> getRunningContainers() {
        return runningContainers;
    }

    // Bindings and properties
    public ListProperty<String> selectedImageTagsProperty() {
        return selectedImageTags;
    }

    public ObjectProperty<File> dockerFileProperty() {
        return dockerFile;
    }

    public StringProperty dockerFileTextProperty() {
        return dockerFileText;
    }

    public StringProperty outputProperty() {
        return output;
    }

    public StringProperty currentContainerIdProperty() {
        return currentContainerId;
    }

    public StringProperty selectedImageProperty() {
        return selectedImage;
    }

    public StringProperty selectedTagProperty() {
        return selectedTag;
    }

    public StringProperty formContainerNameProperty() {
        return formContainerName;
    }

    public StringProperty formContainerPortProperty() {
        return formContainerPort;
    }

    public StringProperty formContainerHostPortProperty() {
        return formContainerHostPort;
    }

    public BooleanProperty dockerConnectedProperty() {
        return dockerConnected;
    }

    public BooleanBinding imageSelectedProperty() {
        return imageSelected;
    }




    // Container object management
    public void addDockerImageTags(DockerImageObject dockerImage) {
        dockerImages.add(dockerImage);
    }


    public void addContainerToList(DockerContainerObject container) {
        runningContainers.add(container);
    }

    public void updateContainerStatus(String containerId, Boolean status) {
        runningContainers.stream()
                .filter(container -> container.getContainerId().equals(containerId))
                .findFirst()
                .ifPresent(container -> container.runningProperty().set(status));
    }

    public void updateContainerListeningStatus(String containerId, Boolean status) {
        runningContainers.forEach(container -> {
            if (container.getContainerId().equals(containerId)) {
                container.listeningProperty().set(status);
            } else {
                container.listeningProperty().set(false);
            }
        });
    }

    public void removeContainerFromList(String containerId) {
        runningContainers.removeIf(container -> container.getContainerId().equals(containerId));
    }
}
