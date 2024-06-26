package nivohub.devinspector.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.util.Builder;
import nivohub.devinspector.docker.DockerContainerObject;
import nivohub.devinspector.model.DockerModel;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;


public class DockerViewBuilder implements Builder<Region> {
    private final Runnable dockerFileBuildAction;
    private final DockerModel model;
    private final Runnable pullAndRunContainerAction;
    private final Runnable saveFileAction;
    private final Runnable exportFileAction;
    private final Consumer<File> uploadFileAction;
    private final Consumer<String> openBrowserToContainerBindings;
    private final Consumer<String> startContainerAction;
    private final Consumer<String> stopContainerAction;
    private final Consumer<String> removeContainerAction;
    private final Consumer<String> streamContainerAction;

    private enum CenterTabs {
        EDITOR,
        OUTPUT
    }

    private final ObjectProperty<CenterTabs> currentCenterTab = new SimpleObjectProperty<>(CenterTabs.OUTPUT);

    public DockerViewBuilder(DockerModel model, Runnable pullAndRunContainerAction, Runnable dockerFileBuildAction, Consumer<String> openBrowserToContainerBindings, Consumer<File> uploadFileAction, Runnable saveFileAction, Runnable exportFileAction, Consumer<String> startContainerAction, Consumer<String> stopContainerAction, Consumer<String> removeContainerAction, Consumer<String> streamContainerAction) {
        this.model = model;
        this.openBrowserToContainerBindings = openBrowserToContainerBindings;
        this.pullAndRunContainerAction = pullAndRunContainerAction;
        this.dockerFileBuildAction = dockerFileBuildAction;
        this.uploadFileAction = uploadFileAction;
        this.saveFileAction = saveFileAction;
        this.exportFileAction = exportFileAction;
        this.startContainerAction = startContainerAction;
        this.stopContainerAction = stopContainerAction;
        this.removeContainerAction = removeContainerAction;
        this.streamContainerAction = streamContainerAction;
    }
    @Override
    public Region build() {
        BorderPane results = new BorderPane();
        results.setCenter(setupCenter());
        results.setLeft(setupLeft());
        results.setRight(setupRight());
        return results;
    }

    //Regions
    private Region setupCenter() {
        TabPane tabPane = createTabPane(createOutputTab(), createEditorTab());
        setupTabSelectionListener(tabPane);
        subscribeToCenterTabChanges(tabPane);
        return createRegionPane(tabPane);
    }

    private Region setupLeft() {
        Node tabPane = createTabPane(createDefualtTab(), createDockerfileTab());
        VBox content = new VBox();
        content.getChildren().addAll(tabPane);
        return createRegionPane(content, new Insets(12));
    }

    private Region setupRight() {
        return createRegionPane(createContainerRegion(), new Insets(0,0,0,12));
    }

    //Tabs
    private Tab createDockerfileTab() {
        Tab results = new Tab("Dockerfile");
        Node uploadButton = styledRunnableButton("Upload Dockerfile", () -> showHandleFileUploadDialog(uploadFileAction));
        Node saveButton = styledRunnableButton("Save Dockerfile", saveFileAction);
        Node exportButton = styledRunnableButton("Export Dockerfile", () -> showHandleFileExportDialog(exportFileAction));
        Line line = new Line(0, 0, 200, 0);
        line.setStroke(Color.web("#0071F3"));
        Node contentTop = styledVbox(List.of(uploadButton, saveButton, exportButton, line), Pos.TOP_CENTER);

        Node containerName = styledTextField("Container Name", model.dockerfileContainerNameProperty());
        Node hostPort = styledTextField("Host Port", model.dockerfileHostPortProperty());
        Node containerPort = styledTextField("Container Port", model.dockerfileContainerPortProperty());
        Node runButton = styledRunnableButton("Build and Run Container from File", dockerFileBuildAction);
        Node contentBottom = styledVbox(List.of(containerName, hostPort, containerPort, runButton), Pos.TOP_CENTER);
        Node box = new VBox(contentTop, contentBottom);
        results.setContent(box);
        return results;
    }

    private Tab createDefualtTab() {
        Tab results = new Tab("Default");

        Node imageSelection = styledComboBox("Select Image", model.getDockerImageNames(), model.selectedImageProperty());
        Node tagSelection = styledComboBox("Select Tag", model.selectedImageTagsProperty(), model.selectedTagProperty());
        tagSelection.disableProperty().bind(model.imageSelectedProperty().not());
        Node containerName = styledTextField("Container Name", model.formContainerNameProperty());
        Node containerPort = styledTextField("Container Port", model.formContainerPortProperty());
        Node hostPort = styledTextField("Host Port", model.formContainerHostPortProperty());
        Node runButton = styledRunnableButton("Run", pullAndRunContainerAction);
        runButton.disableProperty().bind(model.dockerConnectedProperty().not());
        List<Node> children = List.of(imageSelection, tagSelection, containerName, hostPort, containerPort, runButton);
        Node content = styledVbox(children, Pos.TOP_CENTER);
        results.setContent(content);
        return results;
    }

    private Tab createEditorTab() {
        Tab results = new Tab("Editor");
        Node content = createContentPane(createTextEditor());
        results.setContent(content);
        return results;
    }

    private Tab createOutputTab() {
        Tab results = new Tab("Docker Output");
        Node content = createContentPane(createOutputArea());

        results.setContent(content);
        return results;
    }

    //Builders

    private Accordion createContainerRegion(){
        Accordion results = new Accordion();
        ObservableList<TitledPane> titledPanes = FXCollections.observableArrayList();
        Bindings.bindContent(results.getPanes(), titledPanes);

        addExistingContainers(titledPanes);
        addContainerChangeListener(titledPanes);

        results.setPrefWidth(300);
        return results;
    }

    private TitledPane createContainerDetails(DockerContainerObject container) {

        // Container details
        Node idLabel = styledLabel("Container ID: "+ container.getContainerId());
        Node nameLabel = styledLabel("Container Name: " + container.getContainerName());
        Node imageLabel = styledLabel("Container Image: " + container.getImage());

        //Bound container status
        StringBinding statusBinding = Bindings.createStringBinding(() -> container.runningProperty().get() ? "Running" : "Stopped", container.runningProperty());
        StringBinding boundTitle = Bindings.createStringBinding(() -> {
            String status = container.runningProperty().get() ? "Running" : "Stopped";
            return container.getContainerName()+" : " + status;
        }, container.runningProperty());

        // Hyperlink to open browser to container bindings
        Node portLabel = styledLabel("Configured Port (Click Me!): ");
        Hyperlink portLink = new Hyperlink("http://localhost:"+ container.getHostPort());
        portLink.setOnAction(e -> openBrowserToContainerBindings.accept(container.getContainerId()));

        // Buttons
        Node streamContainerLogsButton = styledRunnableButton("Stream Logs", () -> streamContainerAction.accept(container.getContainerId()));
        Node startContainerButton = styledRunnableButton("Start", () -> startContainerAction.accept(container.getContainerId()));
        Node stopContainerButton = styledRunnableButton("Stop", () -> stopContainerAction.accept(container.getContainerId()));

        // Disable stream logs button if container is already streaming logs
        streamContainerLogsButton.disableProperty().bind(container.listeningProperty());

        startContainerButton.disableProperty().bind(container.runningProperty());
        stopContainerButton.disableProperty().bind(container.runningProperty().not());

        // Disable remove button if container is running
        Node removeContainerButton = styledRunnableButton("Remove", () -> removeContainerAction.accept(container.getContainerId()));
        removeContainerButton.disableProperty().bind(container.runningProperty());

        Label status = new Label();
        status.textProperty().bind(statusBinding);

        return styledTitledPane(boundTitle, List.of(nameLabel, idLabel, imageLabel, status, portLabel, portLink, streamContainerLogsButton, startContainerButton, stopContainerButton, removeContainerButton));
    }

    private TabPane createTabPane(Tab firstTab, Tab secondTab ) {
        TabPane results = new TabPane();
        results.getTabs().addAll(firstTab, secondTab);
        return results;
    }

    private Region createRegionPane(Node child, Insets insets) {
        AnchorPane results = new AnchorPane();
        results.getChildren().addAll(child);
        results.setPadding(insets);

        AnchorPane.setBottomAnchor(child, 0.0);
        AnchorPane.setTopAnchor(child, 0.0);
        AnchorPane.setLeftAnchor(child, 0.0);
        AnchorPane.setRightAnchor(child, 0.0);

        return results;
    }

    private Node createOutputArea() {
        ListView<String> result = new ListView<>();
        result.setMaxHeight(Double.MAX_VALUE);
        result.setItems(model.outputListProperty());

        // Add a listener to the ObservableList from model
        model.outputListProperty().addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    // Scroll to the last item
                    result.scrollTo(model.outputListProperty().size() - 1);
                }
            }
        });

        return result;
    }

    private Node createTextEditor() {
        TextArea result = new TextArea();
        result.setWrapText(true);
        result.textProperty().bindBidirectional(model.dockerFileTextProperty());
        return result;
    }

    //Events and Helpers
    private void addExistingContainers(ObservableList<TitledPane> titledPanes) {
        for (DockerContainerObject container : model.getDockerContainers()) {
            TitledPane pane = createContainerDetails(container);
            titledPanes.add(pane);
        }
    }

    private void addContainerChangeListener(ObservableList<TitledPane> titledPanes) {
        model.getDockerContainers().addListener((ListChangeListener.Change<? extends DockerContainerObject> c) -> {
            while (c.next()) {
                handleAddedContainers(titledPanes, c);
                handleRemovedContainers(titledPanes, c);
            }
        });
    }

    private void handleAddedContainers(ObservableList<TitledPane> titledPanes, ListChangeListener.Change<? extends DockerContainerObject> c) {
        if (c.wasAdded()) {
            for (DockerContainerObject container : c.getAddedSubList()) {
                Platform.runLater(() -> {
                    TitledPane pane = createContainerDetails(container);
                    titledPanes.add(pane);
                });
            }
        }
    }

    private void handleRemovedContainers(ObservableList<TitledPane> titledPanes, ListChangeListener.Change<? extends DockerContainerObject> c) {
        if (c.wasRemoved()) {
            for (DockerContainerObject container : c.getRemoved()) {
                Platform.runLater(() -> titledPanes.removeIf(pane -> pane.getText().contains(container.getContainerName())));
            }
        }
    }

    private void showHandleFileUploadDialog(Consumer<File> consumer) {
        currentCenterTab.set(CenterTabs.EDITOR);
        FileChooser results = createDockerfileChooser("Upload Dockerfile");
        File file = results.showOpenDialog(null);
        if (file != null) {
            consumer.accept(file);
        }
    }

    private void showHandleFileExportDialog(Runnable runnable) {
        FileChooser results = createDockerfileChooser("Export Dockerfile");
        results.setInitialFileName(model.dockerFileProperty().get().getName());
        File file = results.showSaveDialog(null);
        if (file != null) {
            runnable.run();
        }
    }

    private void setupTabSelectionListener(TabPane tabPane) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab.getText().equalsIgnoreCase("Editor")) {
                currentCenterTab.set(CenterTabs.EDITOR);
            } else {
                currentCenterTab.set(CenterTabs.OUTPUT);
            }
        });
    }

    private void subscribeToCenterTabChanges(TabPane tabPane) {
        currentCenterTab.subscribe(newTab -> {
            if (newTab == CenterTabs.EDITOR) {
                selectTabByIndex(tabPane, 1);
            } else {
                selectTabByIndex(tabPane, 0);
            }
        });
    }

    private void selectTabByIndex(TabPane tabPane, int index) {
        tabPane.getSelectionModel().select(tabPane.getTabs().get(index));
    }

    //Styling
    private FileChooser createDockerfileChooser(String title) {
        FileChooser results = new FileChooser();
        results.setTitle(title);
        results.getExtensionFilters().add(new FileChooser.ExtensionFilter("Dockerfile", "Dockerfile"));
        results.setInitialDirectory(new File("src/main/resources/dockerfiles"));
        return results;
    }

    private TitledPane styledTitledPane(StringBinding title, List<Node> content) {
        TitledPane results = new TitledPane();
        results.textProperty().bind(title);
        results.setContent(styledVbox(content, Pos.TOP_LEFT));
        return results;
    }

    private Region createRegionPane(Node child) {
        AnchorPane results = new AnchorPane();
        results.getChildren().addAll(child);

        AnchorPane.setBottomAnchor(child, 0.0);
        AnchorPane.setTopAnchor(child, 0.0);
        AnchorPane.setLeftAnchor(child, 0.0);
        AnchorPane.setRightAnchor(child, 0.0);
        return results;
    }

    private Node createContentPane(Node child) {
        AnchorPane results = new AnchorPane();
        results.getChildren().addAll(child);

        AnchorPane.setBottomAnchor(child, 0.0);
        AnchorPane.setTopAnchor(child, 0.0);
        AnchorPane.setLeftAnchor(child, 0.0);
        AnchorPane.setRightAnchor(child, 0.0);

        return results;
    }

    private Node styledVbox(List<Node> children, Pos alignment) {
        VBox results = new VBox(24);
        results.setPadding(new Insets(20,12,12,12));
        results.fillWidthProperty().set(false);
        results.setAlignment(alignment);
        results.getChildren().addAll(children);
        return results;
    }

    private Node styledTextField(String prompt, StringProperty binding) {
        TextField results = new TextField();
        results.setPromptText(prompt);
        results.setPrefWidth(150);
        results.textProperty().bindBidirectional(binding);
        return results;
    }

    private Node styledRunnableButton(String label, Runnable action) {
        Button results = new Button(label);
        results.setPrefWidth(150);
        results.setOnAction(evt -> action.run());
        results.disableProperty().bind(model.threadBuildingProperty());
        return results;
    }

    private Node styledComboBox(String prompt, ObservableList<String> items, StringProperty binding) {
        ComboBox<String> results = new ComboBox<>();
        results.setPromptText(prompt);
        results.setPrefWidth(150);
        results.setItems(items);
        results.valueProperty().bindBidirectional(binding);
        return results;
    }

    private Node styledLabel(String label) {
        return new Label(label);
    }

}
