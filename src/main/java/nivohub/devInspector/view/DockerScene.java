package nivohub.devInspector.view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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

//    public Scene createScene() {
//        MenuBar menuBar = appMenu.createMenu();
//        layout.getChildren().addAll(menuBar, imageSelection, portInput, tagSelection, outputArea);
//        return new Scene(layout, 800, 600);
//    }


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

    public Scene createScene(){
        layout.setPrefSize(900, 600);

        //Layout
        SplitPane splitPane = new SplitPane();
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        splitPane.setDividerPositions(0.25, 0.75);

        // Left pane
        AnchorPane leftPane = new AnchorPane();
        Label configurationLabel = new Label("Configuration");
        configurationLabel.setLayoutX(14);
        configurationLabel.setLayoutY(14);
        configurationLabel.setFont(new Font(18));
        configurationLabel.setTextFill(Color.color(0.624, 0.624, 0.624));

        //Left Content
        VBox leftVBox = new VBox(24);
        leftVBox.setLayoutX(14);
        leftVBox.setLayoutY(43);
        imageSelection.setPrefWidth(150);
        tagSelection.setPrefWidth(150);
        portInput.setPrefWidth(150);
        portInput.setPromptText("Port e.g. 8080");
        leftVBox.getChildren().addAll(imageSelection, tagSelection, portInput, runButton);
        leftPane.getChildren().addAll(configurationLabel, leftVBox);


        AnchorPane centerPane = new AnchorPane();
        AnchorPane rightPane = new AnchorPane();

        splitPane.getItems().addAll(leftPane, centerPane, rightPane);

        HBox statusBar = new HBox();
        Label leftStatus = new Label("Left status");
        Label rightStatus = new Label("Right status");
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        statusBar.getChildren().addAll(leftStatus, spacer, rightStatus);
        statusBar.setPadding(new Insets(3));

        MenuBar menuBar = appMenu.createMenu();
        layout.getChildren().addAll(menuBar, splitPane, statusBar);
        return new Scene(layout);
    }
}