package nivohub.devinspector.docker;

import javafx.collections.ObservableList;

public record DockerImage(String imageName, ObservableList<String> tags) {
}