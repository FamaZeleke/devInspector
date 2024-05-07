package nivohub.devInspector.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DockerScene extends BaseScene {
    private final Accordion containerDetailsAccordion = new Accordion();
    private final AppMenu appMenu;
    private final VBox layout = new VBox();
    private final AnchorPane centerPane = new AnchorPane();
    private final TabPane configTabPane = new TabPane();
    private final TabPane centerTabbedPane = new TabPane();
    private final Tab defaultConfigTab = new Tab("Default Config");
    private final Tab dockerfileTab = new Tab("Dockerfile");
    private final Tab outputLogsTab = new Tab("Output Logs");
    private final Tab editorTab = new Tab("Editor");
    private final TextArea editDockerArea = new TextArea();
    private final ListView<String> outputArea = new ListView<>();
    private final ListView<String> volumeDirList = new ListView<>();
    private final ObservableList<String> volumeDirItems = volumeDirList.getItems();
    private final ComboBox<String> imageSelection = new ComboBox<>();
    private final ComboBox<String> tagSelection = new ComboBox<>();
    private final TextField exposedPort = new TextField();
    private final TextField hostPort = new TextField();
    private final TextField containerName = new TextField();
    private final Image uploadIcon = new Image("/icon/file-up.png");
    private final ImageView uploadIconView = new ImageView(uploadIcon);
    private final Button uploadButton = new Button("Upload Dockerfile", uploadIconView);
    private final Button exportDockerfileButton = new Button("Export Dockerfile");
    private final Button runButton = new Button("Run");
    private final Button runDockerfileButton = new Button("Run Dockerfile");
    private final Button editVolumesButton = new Button("Edit Volumes");
    private final Button addVolumeDirButton = new Button("Add Volume Directory");
    private final Button removeDirectoryButton = new Button("Remove Selected Directory");
    private DockerController controller;

    public DockerScene(AppMenu appMenu) {
        super(appMenu);
        this.appMenu = appMenu;
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

    @Override
    public void setController(Object controller) {
        if (controller instanceof DockerController) {
            this.controller = (DockerController) controller;
        } else {
            throw new IllegalArgumentException("Controller must be a DockerController");
        }
    }

    // Main method to create the scene
    @Override
    public Scene createScene() {
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

    // Set up the left pane with the configuration tab

    private AnchorPane setupLeftPane() {
        AnchorPane leftPane = new AnchorPane();
        leftPane.setPadding(new Insets(0,8,0,8));

        Label configurationLabel = new Label("Configuration");
        configurationLabel.setLayoutY(5);
        configurationLabel.setLayoutX(75);
        configurationLabel.setFont(new Font(18));
        configurationLabel.setTextFill(Color.color(0.624, 0.624, 0.624));

        Tab defaultConfigTab = getDefaultConfigTab();

        Tab dockerfileTab = getDockerfileTab();

        configTabPane.getTabs().addAll(defaultConfigTab, dockerfileTab);

        leftPane.getChildren().addAll(configurationLabel, configTabPane);
        return leftPane;
    }

    private Tab getDockerfileTab() {
        VBox dockerfileVBox = new VBox(24);
        dockerfileVBox.setAlignment(Pos.TOP_CENTER);
        dockerfileVBox.setPadding(new Insets(8));
        uploadButton.setPrefWidth(150);
        uploadButton.setOnAction(this::eventOnFileUpload);

        exportDockerfileButton.setPrefWidth(150);
        exportDockerfileButton.setOnAction(this::eventOnExportDockerfile);

        runDockerfileButton.setPrefWidth(150);

        dockerfileVBox.getChildren().addAll(uploadButton, exportDockerfileButton, runDockerfileButton);
        dockerfileTab.setContent(dockerfileVBox);
        return dockerfileTab;
    }

    private Tab getDefaultConfigTab() {
        configTabPane.setLayoutX(14);
        configTabPane.setLayoutY(43);

        imageSelection.setPrefWidth(150);
        imageSelection.setPromptText("Select Image");
        tagSelection.setPrefWidth(150);
        tagSelection.setPromptText("Select Tag");

        containerName.setPrefWidth(150);
        containerName.setPromptText("Container Name");

        hostPort.setPrefWidth(150);
        hostPort.setPromptText("Host Port e.g. 8080");
        exposedPort.setPrefWidth(150);
        exposedPort.setPromptText("Exposed Port e.g. 80");
        editVolumesButton.setPrefWidth(150);
        editVolumesButton.setOnAction(e -> showDirectoryChooserDialog());

        // Predefined list tab
        VBox predefinedListVBox = new VBox(24);
        predefinedListVBox.getChildren().addAll(imageSelection, tagSelection, containerName, hostPort, exposedPort, editVolumesButton, runButton);
        defaultConfigTab.setContent(predefinedListVBox);
        return defaultConfigTab;
    }

    // Set up the center pane with the output logs and editor tabs

    private AnchorPane setupCenterPane() {
        Tab outputLogsTab = getOutputLogsTab();
        Tab editorTab = getEditorTab();

        centerTabbedPane.getTabs().addAll(outputLogsTab, editorTab);

        AnchorPane.setTopAnchor(centerTabbedPane, 0.0);
        AnchorPane.setRightAnchor(centerTabbedPane, 0.0);
        AnchorPane.setBottomAnchor(centerTabbedPane, 0.0);
        AnchorPane.setLeftAnchor(centerTabbedPane, 0.0);

        centerPane.getChildren().addAll(centerTabbedPane);

        return centerPane;
    }

    private Tab getEditorTab() {
        editorTab.setContent(editDockerArea);

        return  editorTab;
    }

    private Tab getOutputLogsTab() {
        AnchorPane outputPane = new AnchorPane();
        outputPane.getChildren().add(outputArea);
        AnchorPane.setTopAnchor(outputArea, 0.0);
        AnchorPane.setRightAnchor(outputArea, 0.0);
        AnchorPane.setBottomAnchor(outputArea, 0.0);
        AnchorPane.setLeftAnchor(outputArea, 0.0);

        outputLogsTab.setContent(outputPane);

        return outputLogsTab;
    }

    // Set up the right pane and method for container details accordion
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

    // Method to show the directory chooser dialog
    private void showDirectoryChooserDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox vbox = setupDirectoryChooser();

        Scene dialogScene = new Scene(vbox, 330, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    // Method to set up the directory chooser dialog
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

    // Methods to handle adding and removing a directory from the volume list
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

    // Methods to handle the file upload and export of the Dockerfile
    private void eventOnFileUpload(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Dockerfile");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Dockerfile", "*"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String content = readFileContent(file);
            editDockerArea.setText(content);
            centerTabbedPane.getSelectionModel().select(editorTab);
        }
    }

    private String readFileContent(File file) {
        // Read the content of the file and return it as a string
        try {
            return Files.readString(Paths.get(file.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void eventOnExportDockerfile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Dockerfile");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Dockerfile", "*"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                PrintWriter writer;
                writer = new PrintWriter(file);
                writer.println(editDockerArea.getText());
                writer.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Dockerfile has been saved.");
                alert.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while saving the Dockerfile.");
                alert.showAndWait();
            }
        }
    }

    // Getter for the image selection dropdown
    public ComboBox<String> getImageSelection() {
        return imageSelection;
    }

    // Getter for the port input field
    public TextField getExposedPort() {
        return exposedPort;
    }

    // Getter for the host port input field
    public TextField getHostPort() {
        return hostPort;
    }

    // Getter for the tag selection dropdown
    public ComboBox<String> getTagSelection() {
        return tagSelection;
    }

    // Getter for the container name input field
    public TextField getContainerName() {
        return containerName;
    }

    public Button getRunButton() {
        return runButton;
    }

    public ListView<String> getOutputArea() {
        return outputArea;
    }

}