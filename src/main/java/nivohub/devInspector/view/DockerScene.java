package nivohub.devInspector.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import nivohub.devInspector.controller.DockerController;

public class DockerScene extends BaseScene {
    private final VBox layout = new VBox();
    private final ComboBox<String> imageSelection = new ComboBox<>();
    private final TextField exposedPort = new TextField();
    private final TextField hostPort = new TextField();
    private final ComboBox<String> tagSelection = new ComboBox<>();
    private final Button runButton = new Button("Run");
    private final ListView<String> outputArea = new ListView<>();
    private final AppMenu appMenu;
    private final Accordion containerDetailsAccordion = new Accordion();
    private DockerController controller;

    public DockerScene(AppMenu appMenu) {
        super(appMenu);
        this.appMenu = appMenu;
    }

    @Override
    public void setController(Object controller) {
        if (controller instanceof DockerController) {
            this.controller = (DockerController) controller;
        } else {
            throw new IllegalArgumentException("Controller must be a DockerController");
        }
    }

    @Override
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
        hostPort.setPrefWidth(150);
        hostPort.setPromptText("Host Port e.g. 8080");
        exposedPort.setPrefWidth(150);
        exposedPort.setPromptText("Exposed Port e.g. 80");
        leftVBox.getChildren().addAll(imageSelection, tagSelection, hostPort, exposedPort, runButton);
        leftPane.getChildren().addAll(configurationLabel, leftVBox);


        // Center pane
        AnchorPane centerPane = new AnchorPane();
        outputArea.setPrefSize(200, 200); // Set preferred size as needed
        AnchorPane.setTopAnchor(outputArea, 0.0);
        AnchorPane.setRightAnchor(outputArea, 0.0);
        AnchorPane.setBottomAnchor(outputArea, 0.0);
        AnchorPane.setLeftAnchor(outputArea, 0.0);
        centerPane.getChildren().add(outputArea);

        // Right pane
        VBox rightVBox = new VBox();
        rightVBox.setLayoutX(14);
        rightVBox.setLayoutY(43);
        AnchorPane rightPane = new AnchorPane();
        AnchorPane.setTopAnchor(containerDetailsAccordion, 0.0);
        AnchorPane.setRightAnchor(containerDetailsAccordion, 0.0);
        AnchorPane.setBottomAnchor(containerDetailsAccordion, 0.0);
        AnchorPane.setLeftAnchor(containerDetailsAccordion, 0.0);
        rightPane.getChildren().add(containerDetailsAccordion);

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

    public void addContainerDetails(String containerId, int hostPort) {
        TitledPane containerPane = new TitledPane();
        containerPane.setText("Container: " + containerId);

        VBox containerDetailsBox = new VBox(5); // 5 is the spacing between elements
        containerDetailsBox.setPadding(new Insets(5));

        // Add various details to the container details box
        Label idLabel = new Label("Container ID: " + containerId);
        Label portLabel = new Label("Configured Port (Click Me!): ");
        Hyperlink portLink = new Hyperlink(String.valueOf(hostPort));
        portLink.setOnAction(event -> {
            // Action to open the browser or perform some operation
            controller.openBrowserToPort(hostPort);
        });

        containerDetailsBox.getChildren().addAll(idLabel, portLabel, portLink);

        containerPane.setContent(containerDetailsBox);

        Platform.runLater(() -> containerDetailsAccordion.getPanes().add(containerPane));
    }


    // Getter for the image selection dropdown
    public ComboBox<String> getImageSelection() {
        return imageSelection;
    }

    // Getter for the port input field
    public TextField getExposedPort() {
        return exposedPort;
    }

    public TextField getHostPort() {
        return hostPort;
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