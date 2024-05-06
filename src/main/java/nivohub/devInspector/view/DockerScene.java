package nivohub.devInspector.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nivohub.devInspector.controller.DockerController;

import java.io.File;

public class DockerScene extends BaseScene {
    private final Accordion containerDetailsAccordion = new Accordion();
    private final AppMenu appMenu;
    private final VBox layout = new VBox();
    private final ListView<String> outputArea = new ListView<>();
    private final ListView<String> volumeDirList = new ListView<>();
    private final ObservableList<String> volumeDirItems = volumeDirList.getItems();
    private final ComboBox<String> imageSelection = new ComboBox<>();
    private final ComboBox<String> tagSelection = new ComboBox<>();
    private final TextField exposedPort = new TextField();
    private final TextField hostPort = new TextField();
    private final Image uploadIcon = new Image("/icon/file-up.png");
    private final ImageView uploadIconView = new ImageView(uploadIcon);
    private final Button runButton = new Button("Run");
    private final Button uploadButton = new Button("", uploadIconView);
    private final Button editVolumesButton = new Button("Edit Volumes");
    private final Button addVolumeDirButton = new Button("Add Volume Directory");
    private final Button removeDirectoryButton = new Button("Remove Selected Directory");
    private final TextField containerName = new TextField();
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
        SplitPane splitPane = createSplitPane();
        HBox statusBar = setupStatusBar();
        MenuBar menuBar = appMenu.createMenu();

        layout.getChildren().addAll(menuBar, splitPane, statusBar);
        return new Scene(layout);
    }

    private SplitPane createSplitPane() {
        SplitPane splitPane = new SplitPane();
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        splitPane.setDividerPositions(0.25, 0.75);
        splitPane.getItems().addAll(
                setupLeftPane(),
                setupCenterPane(),
                setupRightPane());
        return splitPane;
    }

    private static HBox setupStatusBar() {
        HBox statusBar = new HBox();
        Label leftStatus = new Label("Left status");
        Label rightStatus = new Label("Right status");
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        statusBar.getChildren().addAll(leftStatus, spacer, rightStatus);
        statusBar.setPadding(new Insets(3));
        return statusBar;
    }

    private AnchorPane setupRightPane() {
        VBox rightVBox = new VBox();
        rightVBox.setLayoutX(14);
        rightVBox.setLayoutY(43);
        AnchorPane rightPane = new AnchorPane();
        AnchorPane.setTopAnchor(containerDetailsAccordion, 0.0);
        AnchorPane.setRightAnchor(containerDetailsAccordion, 0.0);
        AnchorPane.setBottomAnchor(containerDetailsAccordion, 0.0);
        AnchorPane.setLeftAnchor(containerDetailsAccordion, 0.0);
        rightPane.getChildren().add(containerDetailsAccordion);
        return rightPane;
    }

    private AnchorPane setupLeftPane() {
        AnchorPane leftPane = new AnchorPane();

        Label configurationLabel = new Label("Configuration");
        configurationLabel.setLayoutX(14);
        configurationLabel.setLayoutY(10);
        configurationLabel.setFont(new Font(18));
        configurationLabel.setTextFill(Color.color(0.624, 0.624, 0.624));

        TabPane tabPane = new TabPane();
        tabPane.setLayoutX(14);
        tabPane.setLayoutY(43);

        // Predefined list tab
        Tab predefinedListTab = new Tab("Predefined List");
        VBox predefinedListVBox = new VBox(24);
        predefinedListVBox.getChildren().addAll(imageSelection, tagSelection, containerName, hostPort, exposedPort, editVolumesButton, runButton);
        predefinedListTab.setContent(predefinedListVBox);

        //TODO resolve ui issues and work out conditionality

        // Dockerfile tab
        Tab dockerfileTab = new Tab("Dockerfile");
        VBox dockerfileVBox = new VBox(24);
        uploadButton.setOnAction(this::eventOnFileUpload);
        dockerfileVBox.getChildren().addAll(uploadButton, containerName, hostPort, exposedPort, editVolumesButton, runButton);
        dockerfileTab.setContent(dockerfileVBox);

        tabPane.getTabs().addAll(predefinedListTab, dockerfileTab);

        leftPane.getChildren().addAll(configurationLabel, tabPane);
        return leftPane;
    }

    private AnchorPane setupCenterPane() {
        AnchorPane centerPane = new AnchorPane();
        outputArea.setPrefSize(200, 200); // Set preferred size as needed
        AnchorPane.setTopAnchor(outputArea, 0.0);
        AnchorPane.setRightAnchor(outputArea, 0.0);
        AnchorPane.setBottomAnchor(outputArea, 0.0);
        AnchorPane.setLeftAnchor(outputArea, 0.0);
        centerPane.getChildren().add(outputArea);
        return centerPane;
    }

    private VBox setupDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory for Volume Binding");

        addVolumeDirButton.setOnAction(e -> handleAddDirectory(directoryChooser));
        removeDirectoryButton.setOnAction(e -> handleRemoveDirectory());

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(28);
        buttonBox.getChildren().addAll(addVolumeDirButton, removeDirectoryButton);

        volumeDirList.setItems(volumeDirItems);

        VBox directoryChooserUI = new VBox();
        directoryChooserUI.getChildren().addAll(buttonBox, volumeDirList);

        return directoryChooserUI;
    }

    private void handleRemoveDirectory() {
        int selectedIndex = volumeDirList.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            volumeDirItems.remove(selectedIndex);
        }
    }

    private void handleAddDirectory(DirectoryChooser directoryChooser) {
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            volumeDirItems.add(selectedDirectory.getAbsolutePath());
        }
    }

    private void showDirectoryChooserDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = setupDirectoryChooser();

        Scene dialogScene = new Scene(vbox, 330, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void eventOnFileUpload(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Dockerfile");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Dockerfile", "*"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            controller.handleFileUpload(file);
        }
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

    public ListView<String> getVolumeDirList() {
        return volumeDirList;
    }

    public TextField getContainerName() {
        return containerName;
    }
}