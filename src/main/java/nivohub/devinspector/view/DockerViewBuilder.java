package nivohub.devinspector.view;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import nivohub.devinspector.model.DockerModel;

import java.util.List;
import java.util.Optional;


public class DockerViewBuilder implements Builder<Region> {

    private final DockerModel model;

    public DockerViewBuilder(DockerModel model) {
        this.model = model;
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

        return createRegionPane(tabPane, new Insets(12));
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
        Node content = createVBox(children);
        results.setContent(content);
        return results;
    }

    private Tab createDefualtTab() {
        Tab results = new Tab("Default");

        Node imageSelection = styledComboBox("Select Image", model.getDockerImageNames(), model.selectedImageProperty());
        Node tagSelection = styledComboBox("Select Tag", model.selectedImageTagsProperty(), model.selectedTagProperty());
        tagSelection.disableProperty().bind(model.imageSelectedProperty().not());
        Node containerName = styledTextField("Container Name");
        Node containerPort = styledTextField("Container Port");
        Node hostPort = styledTextField("Host Port");
        Node runButton = styledButton("Run");
        List<Node> children = List.of(imageSelection, tagSelection, containerName, containerPort, hostPort, runButton);
        Node content = createVBox(children);
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

    private Region createContainerRegion(){
        Accordion results = new Accordion();
        return results;
    }

    private Node createContainerDetails() {

        Node idLabel = styledLabel("Container ID: ");
        Node nameLabel = styledLabel("Container Name: ");
        Node imageLabel = styledLabel("Container Image: ");
        Node statusLabel = styledLabel("Container Status: ");
        Node portLabel = styledLabel("Configured Port (Click Me!): ");
        Hyperlink portLink = new Hyperlink("www.google.com");
        return styledTitledPane("Container Details", List.of(idLabel, nameLabel, imageLabel, statusLabel, portLabel, portLink));
    }

    private Region createTabPane(Tab firstTab, Tab secondTab ) {
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
        return new ListView<String>();
    }

    private Node createTextEditor() {
        return new TextArea();
    }


    //Styling
    private Node styledTitledPane(String title, List<Node> content) {
        TitledPane results = new TitledPane();
        results.setText(title);
        results.setContent(createVBox(content));
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

    private Node createVBox(List<Node> children) {
        VBox results = new VBox(24);
        results.setPadding(new Insets(20,12,12,12));
        results.fillWidthProperty().set(false);
        results.setAlignment(Pos.TOP_CENTER);
        results.getChildren().addAll(children);
        return results;
    }

    private Node styledTextField(String prompt) {
        TextField results = new TextField();
        results.setPromptText(prompt);
        results.setPrefWidth(150);
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
