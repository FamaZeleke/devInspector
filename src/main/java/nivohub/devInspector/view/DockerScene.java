package nivohub.devInspector.view;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import nivohub.devInspector.controller.DockerController;
import nivohub.devInspector.controller.SceneController;

public class DockerScene implements SceneController.SceneCreator {
    private final VBox layout = new VBox();
    private final ComboBox<String> imageSelection = new ComboBox<>();
    private final TextField portInput = new TextField();
    private final ComboBox<String> tagSelection = new ComboBox<>();
    private final Button runButton = new Button("Run");
    private final ListView<String> outputArea = new ListView<>();
    private final AppMenu appMenu;
    private DockerController controller;

    public DockerScene(AppMenu appMenu) {
        this.appMenu = appMenu;

    }

    public void setController(DockerController controller) {
        this.controller = controller;
    }

    public Scene createScene() {
        MenuBar menuBar = appMenu.createMenu();
        layout.getChildren().addAll(menuBar, imageSelection, portInput, tagSelection, outputArea);
        return new Scene(layout, 800, 600);
    }


    // Getter for the layout
    public VBox getLayout() {
        return layout;
    }

    // Getter for the image selection dropdown
    public ComboBox<String> getImageSelection() {
        return imageSelection;
    }

    // Getter for the port input field
    public TextField getPortInput() {
        return portInput;
    }

    // Getter for the tag selection dropdown
    public ComboBox<String> getTagSelection() {
        return tagSelection;
    }

    public Button getRunButton() {
        return runButton;
    }

    public ListView<String> getOutputArea() {
        return outputArea;
    }
}