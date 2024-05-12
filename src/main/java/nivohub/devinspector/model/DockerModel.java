package nivohub.devinspector.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nivohub.devinspector.docker.DockerImage;

import java.util.stream.Collectors;

public class DockerModel {

    private final ObservableList<DockerImage> dockerImages;
    private final SimpleStringProperty selectedImage = new SimpleStringProperty();
    private final ListProperty<String> selectedImageTags = new SimpleListProperty<>();
    private final SimpleStringProperty selectedTag = new SimpleStringProperty();
    private final BooleanBinding imageSelected;

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

    public SimpleStringProperty selectedImageProperty() {
        return selectedImage;
    }

    public SimpleStringProperty selectedTagProperty() {
        return selectedTag;
    }

    public ObservableList<DockerImage> getDockerImages() {
        return dockerImages;
    }

    public ObservableList<String> getDockerImageNames() {
        return dockerImages.stream()
                .map(DockerImage::imageName)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public ListProperty<String> selectedImageTagsProperty() {
        return selectedImageTags;
    }

    public BooleanBinding imageSelectedProperty() {
        return imageSelected;
    }

    public void addDockerImage(DockerImage dockerImage) {
        dockerImages.add(dockerImage);
    }

}
