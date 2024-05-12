package nivohub.devinspector.view;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
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
import nivohub.devinspector.docker.DockerImage;
import nivohub.devinspector.model.DockerModel;

import java.util.List;
import java.util.stream.Collectors;


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
        return results;
    }

    //Regions
    private Region setupCenter() {
        AnchorPane results = new AnchorPane();
        Region tabPane = createTabPane(createOutputTab(), createEditorTab());
        results.getChildren().add(tabPane);
        return results;
    }

    private Region setupLeft() {
        AnchorPane results = new AnchorPane();
        results.setPadding(new Insets(12));

        Region tabPane = createTabPane(createDefualtTab(), createDockerfileTab());
        results.getChildren().add(tabPane);
        return results;
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

        Node imageSelection = styledComboBox("Select Image", model.getDockerImageNames());
        Node containerName = styledTextField("Container Name");
        Node containerPort = styledTextField("Container Port");
        Node hostPort = styledTextField("Host Port");
        Node runButton = styledButton("Run");
        List<Node> children = List.of(imageSelection, containerName, containerPort, hostPort, runButton);
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
    private Region createTabPane(Tab firstTab, Tab secondTab ) {
        TabPane results = new TabPane();
        results.getTabs().addAll(firstTab, secondTab);
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
        results.setPadding(new Insets(12));
        results.setAlignment(Pos.TOP_CENTER);
        results.getChildren().addAll(children);
        return results;
    }

    private Node createOutputArea() {
        return new ListView<String>();
    }

    private Node createTextEditor() {
        return new TextArea();
    }

    private Node styledTextField(String prompt) {
        TextArea results = new TextArea();
        results.setPromptText(prompt);
        results.setPrefWidth(148);
        results.setMaxHeight(18);
        return results;
    }

    private Node styledButton(String label) {
        Button results = new Button(label);
        results.setPrefWidth(150);
        return results;
    }

    private Node styledComboBox(String prompt, ObservableList<String> items) {
        ComboBox<String> results = new ComboBox<>();
        results.setPromptText(prompt);
        results.setPrefWidth(150);
        results.itemsProperty().bind(Bindings.createObjectBinding(() -> items, items));
        return results;
    }
}
