package nivohub.devinspector.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nivohub.devinspector.docker.DockerImage;

import java.util.stream.Collectors;

public class DockerModel {

    private ObservableList<DockerImage> dockerImages;

    public DockerModel() {
        this.dockerImages = FXCollections.observableArrayList(
                new DockerImage("kale5/rickroll", FXCollections.observableArrayList("latest","arm64")),
                new DockerImage("ubuntu", FXCollections.observableArrayList("latest","20.04")),
                new DockerImage("jupyter/base-notebook", FXCollections.observableArrayList("latest","arm64"))
        );
    }

    public ObservableList<DockerImage> getDockerImages() {
        return dockerImages;
    }

    public ObservableList<String> getDockerImageNames() {
        return dockerImages.stream()
                .map(DockerImage::imageName)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    public void addDockerImage(DockerImage dockerImage) {
        dockerImages.add(dockerImage);
    }

}
