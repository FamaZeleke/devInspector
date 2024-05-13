package nivohub.devinspector.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.util.Builder;
import nivohub.devinspector.docker.DockerContainer;
import nivohub.devinspector.model.DockerModel;

import java.util.List;
import java.util.function.Consumer;


public class DockerViewBuilder implements Builder<Region> {

    private final DockerModel model;
    private final Runnable connectDockerAction;
    private final Runnable pullAndRunContainerAction;
    private final Consumer<String> openBrowserToContainerBindings;

    public DockerViewBuilder(DockerModel model, Runnable pullAndRunContainerAction, Runnable connectDockerAction, Consumer<String> openBrowserToContainerBindings) {
        this.model = model;
        this.connectDockerAction = connectDockerAction;
        this.pullAndRunContainerAction = pullAndRunContainerAction;
        this.openBrowserToContainerBindings = openBrowserToContainerBindings;
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
        Node tabPane = createTabPane(createOutputTab(), createEditorTab());
        Region results = createRegionPane(tabPane);
        return results;
    }

    private Region setupLeft() {
        Node tabPane = createTabPane(createDefualtTab(), createDockerfileTab());
        Node box = createBox();
        VBox content = new VBox();
        content.getChildren().addAll(tabPane, box);
        return createRegionPane(content, new Insets(12));
    }

    private Region setupRight() {
        return createRegionPane(createContainerRegion(), new Insets(0,0,0,12));
    }

    //Tabs
    private Tab createDockerfileTab() {
        Tab results = new Tab("Dockerfile");
        Node uploadButton = styledButton("Upload Dockerfile");
        Node exportButton = styledButton("Export Dockerfile");
        Node runButton = styledButton("Run");
        List<Node> children = List.of(uploadButton, exportButton, runButton);
        Node content = styledVbox(children, Pos.TOP_CENTER);
        results.setContent(content);
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

    private void addExistingContainers(ObservableList<TitledPane> titledPanes) {
        for (DockerContainer container : model.getRunningContainers()) {
            TitledPane pane = createContainerDetails(container);
            titledPanes.add(pane);
        }
    }

    private void addContainerChangeListener(ObservableList<TitledPane> titledPanes) {
        model.getRunningContainers().addListener((ListChangeListener.Change<? extends DockerContainer> c) -> {
            while (c.next()) {
                handleAddedContainers(titledPanes, c);
                handleRemovedContainers(titledPanes, c);
            }
        });
    }

    private void handleAddedContainers(ObservableList<TitledPane> titledPanes, ListChangeListener.Change<? extends DockerContainer> c) {
        if (c.wasAdded()) {
            for (DockerContainer container : c.getAddedSubList()) {
                Platform.runLater(() -> {
                    TitledPane pane = createContainerDetails(container);
                    titledPanes.add(pane);
                });
            }
        }
    }

    private void handleRemovedContainers(ObservableList<TitledPane> titledPanes, ListChangeListener.Change<? extends DockerContainer> c) {
        if (c.wasRemoved()) {
            for (DockerContainer container : c.getRemoved()) {
                Platform.runLater(() -> titledPanes.removeIf(pane -> pane.getText().equals(container.containerName())));
            }
        }
    }

    private TitledPane createContainerDetails(DockerContainer container) {

        Node idLabel = styledLabel("Container ID: "+ container.containerId());
        Node nameLabel = styledLabel("Container Name: " + container.containerName());
        Node imageLabel = styledLabel("Container Image: " + container.image());
        Node statusLabel = styledLabel("Container Status: " + container.status());
        Node portLabel = styledLabel("Configured Port (Click Me!): ");
        Hyperlink portLink = new Hyperlink("Http://localhost:"+ container.hostPort());
        portLink.setOnAction(e -> openBrowserToContainerBindings.accept(container.containerId()));
        return styledTitledPane( container.containerName() + " : "+container.status(), List.of(nameLabel, idLabel, imageLabel, statusLabel, portLabel, portLink));
    }

    private Region createTabPane(Tab firstTab, Tab secondTab ) {
        TabPane results = new TabPane();
        results.getTabs().addAll(firstTab, secondTab);
        return results;
    }

    private Node createBox() {
        Label label = (Label) styledLabel("Docker is not running");
        label.textProperty().bind(Bindings.when(model.dockerConnectedProperty())
                .then("Docker is running")
                .otherwise("Docker is not running"));
        label.textFillProperty().bind(Bindings.when(model.dockerConnectedProperty())
                .then(javafx.scene.paint.Color.GREEN)
                .otherwise(javafx.scene.paint.Color.RED));
        Node connectDocker = styledRunnableButton("Connect Docker", connectDockerAction);
        connectDocker.disableProperty().bind(model.dockerConnectedProperty());
        return styledVbox(List.of(label, connectDocker), Pos.CENTER);
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
        TextArea output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);
        output.setMaxHeight(Double.MAX_VALUE);
        output.textProperty().bindBidirectional(model.outputProperty());
        model.outputProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(() -> {
                output.selectPositionCaret(output.getLength());
                output.deselect();  // to remove the text selection
            }));
        return output;
    }

    private Node createTextEditor() {
        return new TextArea();
    }


    //Styling
    private TitledPane styledTitledPane(String title, List<Node> content) {
        TitledPane results = new TitledPane();
        results.setText(title);
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

    private Node styledTextField(String prompt, SimpleStringProperty binding) {
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
        return results;
    }

    private Node styledButton(String label) {
        Button results = new Button(label);
        results.setPrefWidth(150);
        return results;
    }

    private Node styledComboBox(String prompt, ObservableList<String> items, SimpleStringProperty binding) {
        ComboBox<String> results = new ComboBox<>();
        results.setPromptText(prompt);
        results.setPrefWidth(150);
        results.itemsProperty().bind(Bindings.createObjectBinding(() -> items, items));
        results.valueProperty().bindBidirectional(binding);
        return results;
    }

    private Node styledLabel(String label) {
        Label results = new Label(label);
        return results;
    }

}
