package nivohub.devinspector.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nivohub.devinspector.docker.DockerContainer;
import nivohub.devinspector.docker.DockerImage;

import java.io.File;
import java.util.stream.Collectors;

public class DockerModel {

    private final ObservableList<DockerImage> dockerImages;
    private final ObservableList<DockerContainer> runningContainers = FXCollections.observableArrayList();
    private final ListProperty<String> selectedImageTags = new SimpleListProperty<>();
    private final ObjectProperty<File> dockerFile = new SimpleObjectProperty<>();
    private final StringProperty dockerFileText = new SimpleStringProperty();
    private final StringProperty output = new SimpleStringProperty("");
    private final StringProperty selectedImage = new SimpleStringProperty();
    private final StringProperty selectedTag = new SimpleStringProperty();
    private final StringProperty formContainerName = new SimpleStringProperty("");
    private final StringProperty formContainerPort = new SimpleStringProperty("");
    private final StringProperty formContainerHostPort = new SimpleStringProperty("");
    private final BooleanBinding imageSelected;
    private final BooleanProperty dockerConnected = new SimpleBooleanProperty(false);

    public DockerModel() {
        this.dockerImages = FXCollections.observableArrayList(
                new DockerImage("kale5/rickroll", FXCollections.observableArrayList("latest","arm64")),
                new DockerImage("ubuntu", FXCollections.observableArrayList("latest","20.04")),
                new DockerImage("jupyter/base-notebook", FXCollections.observableArrayList("latest","arm64"))
        );
        imageSelected = selectedImage.isNotNull();
        selectedImage.addListener((observable, oldValue, newValue) -> {
            DockerImage image = dockerImages.stream()
                    .filter(dockerImage -> dockerImage.imageName().equals(newValue))
                    .findFirst()
                    .orElse(null);
            if (image != null) {
                selectedImageTags.set(FXCollections.observableArrayList(image.tags()));
            } else {
                selectedImageTags.set(FXCollections.observableArrayList());
            }
        });
    }

    public void addToOutput(String newOutput) {
        if (newOutput != null) {
            String currentOutput = this.output.get();
            this.output.setValue(currentOutput+newOutput);
        }
    }

    public ObservableList<String> getDockerImageNames() {
        return dockerImages.stream()
                .map(DockerImage::imageName)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ObservableList<DockerContainer> getRunningContainers() {
        return runningContainers;
    }

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

    public void addDockerImage(DockerImage dockerImage) {
        dockerImages.add(dockerImage);
    }

    public void addContainerToList(DockerContainer container) {
        runningContainers.add(container);
    }
}
