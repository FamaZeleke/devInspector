package nivohub.devinspector.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nivohub.devinspector.docker.DockerContainer;
import nivohub.devinspector.docker.DockerImage;

import java.util.stream.Collectors;

public class DockerModel {

    private final ObservableList<DockerImage> dockerImages;
    private final ObservableList<DockerContainer> runningContainers = FXCollections.observableArrayList();
    private final SimpleStringProperty output = new SimpleStringProperty("");
    private final SimpleStringProperty selectedImage = new SimpleStringProperty();
    private final ListProperty<String> selectedImageTags = new SimpleListProperty<>();
    private final SimpleStringProperty selectedTag = new SimpleStringProperty();
    private final BooleanBinding imageSelected;
    private final SimpleBooleanProperty dockerConnected = new SimpleBooleanProperty(false);
    private final SimpleStringProperty formContainerName = new SimpleStringProperty("");
    private final SimpleStringProperty formContainerPort = new SimpleStringProperty("");
    private final SimpleStringProperty formContainerHostPort = new SimpleStringProperty("");

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

    public SimpleStringProperty outputProperty() {
        return output;
    }

    public SimpleStringProperty selectedImageProperty() {
        return selectedImage;
    }

    public SimpleStringProperty selectedTagProperty() {
        return selectedTag;
    }

    public SimpleStringProperty formContainerNameProperty() {
        return formContainerName;
    }

    public SimpleStringProperty formContainerPortProperty() {
        return formContainerPort;
    }

    public SimpleStringProperty formContainerHostPortProperty() {
        return formContainerHostPort;
    }

    public BooleanBinding imageSelectedProperty() {
        return imageSelected;
    }

    public SimpleBooleanProperty dockerConnectedProperty() {
        return dockerConnected;
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

    public void addDockerImage(DockerImage dockerImage) {
        dockerImages.add(dockerImage);
    }

    public void addContainerToList(DockerContainer Container) {
        runningContainers.add(Container);
    }
}
