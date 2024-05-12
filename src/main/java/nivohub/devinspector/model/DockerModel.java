package nivohub.devinspector.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nivohub.devinspector.docker.DockerImage;

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
}
