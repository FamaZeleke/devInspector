package nivohub.devinspector.view;

import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import javafx.util.Callback;
import nivohub.devinspector.docker.DockerContainer;
import nivohub.devinspector.interfaces.DockerInterface;
import nivohub.devinspector.model.DockerModel;

public class HomeViewBuilder implements Builder<Region> {

private final DockerModel model;
    private final DockerInterface dockerInterface;

    public HomeViewBuilder(DockerModel dockerModel, DockerInterface dockerInterface) {
        this.model = dockerModel;
        this.dockerInterface = dockerInterface;
    }

    @Override
    public Region build() {
        TabPane results = new TabPane();
        results.getTabs().addAll(createImageTab(), createContainerTab());
        results.setSide(Side.LEFT);
        return results;
    }

    private Tab createContainerTab() {
        Tab containerTab = new Tab("Containers");
        containerTab.setContent(createContainerTable());
        return containerTab;
    }

    private Tab createImageTab() {
        Tab imageTab = new Tab("Images");
        return imageTab;
    }

    private TableView<DockerContainer> createContainerTable() {
        TableView<DockerContainer> table = new TableView<>();

        table.setItems(model.getRunningContainers());

        TableColumn<DockerContainer, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("containerName"));
        nameColumn.setPrefWidth(200);

        TableColumn<DockerContainer, String> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        imageColumn.setPrefWidth(150);

        TableColumn<DockerContainer, Boolean> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().runningProperty());
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item ? "Running" : "Stopped");
                }
            }
        });

        TableColumn<DockerContainer, String> cpuColumn = new TableColumn<>("CPU %");
        cpuColumn.setCellValueFactory(new PropertyValueFactory<>("cpu"));

        TableColumn<DockerContainer, String> hostPortColumn = new TableColumn<>("Host Port");
        hostPortColumn.setCellValueFactory(new PropertyValueFactory<>("hostPort"));
        hostPortColumn.setPrefWidth(100);

        TableColumn<DockerContainer, String> exposedPortColumn = new TableColumn<>("Exposed Port");
        exposedPortColumn.setCellValueFactory(new PropertyValueFactory<>("exposedPort"));
        exposedPortColumn.setPrefWidth(100);

        TableColumn<DockerContainer, Void> toggleColumn = new TableColumn<>("Toggle");
        Callback<TableColumn<DockerContainer, Void>, TableCell<DockerContainer, Void>> toggleCellFactory =
                param -> new TableCell<>() {
                    private final Button btn = new Button();

                    {
                        btn.setOnAction(event -> {
                            DockerContainer data = getTableView().getItems().get(getIndex());
                            if (data.runningProperty().get()) {
                                dockerInterface.stopContainer(data.getContainerId());
                            } else {
                                dockerInterface.startContainer(data.getContainerId());
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            DockerContainer data = getTableView().getItems().get(getIndex());
                            btn.setText(data.runningProperty().get() ? "Stop" : "Start");
                            setGraphic(btn);
                        }
                    }
                };
        toggleColumn.setCellFactory(toggleCellFactory);

        TableColumn<DockerContainer, Void> removeColumn = new TableColumn<>("Remove");
        Callback<TableColumn<DockerContainer, Void>, TableCell<DockerContainer, Void>> removeCellFactory =
                param -> new TableCell<>() {
                    private final Button btn = new Button("Remove");

                    {
                        btn.setOnAction(event -> {
                            DockerContainer data = getTableView().getItems().get(getIndex());
                            dockerInterface.removeContainer(data.getContainerId());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
        removeColumn.setCellFactory(removeCellFactory);

        table.getColumns().add(nameColumn);
        table.getColumns().add(imageColumn);
        table.getColumns().add(statusColumn);
        table.getColumns().add(cpuColumn);
        table.getColumns().add(hostPortColumn);
        table.getColumns().add(exposedPortColumn);
        table.getColumns().add(toggleColumn);
        table.getColumns().add(removeColumn);

        // TODO: Add table to your layout
        return table;
    }

    private void createImageTable() {

    }

    private Region styledTable() {
        Region table = new TableView<DockerContainer>();
        return table;
    }


}
