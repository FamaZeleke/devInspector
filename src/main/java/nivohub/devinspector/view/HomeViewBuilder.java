package nivohub.devinspector.view;

import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import javafx.util.Callback;
import nivohub.devinspector.docker.DockerContainerObject;
import nivohub.devinspector.docker.DockerImageObject;
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
        imageTab.setContent(createImageTable());
        return imageTab;
    }

    // Create a table view for the Docker images
    private TableView<DockerImageObject> createImageTable() {
        TableView<DockerImageObject> table = new TableView<>();
        table.setItems(model.getDockerImages());
        table.autosize();

        TableColumn<DockerImageObject, String> idColumn = createCopyableColumn("ID", "imageId", 250);

        TableColumn<DockerImageObject, String> nameColumn = createCopyableColumn("Name", "imageName", 150);

        TableColumn<DockerImageObject, String> tagsColumn = createCopyableColumn("Tags", "tagsAsString", 150);

        TableColumn<DockerImageObject, String> containerColumn = createCopyableColumn("Container", "container", 150);

        TableColumn<DockerImageObject, String> architectureColumn = createColumn("Arch", "architecture", 150);

        TableColumn<DockerImageObject, String> osColumn = createColumn("OS", "os", 150);


        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(tagsColumn);
        table.getColumns().add(containerColumn);
        table.getColumns().add(architectureColumn);
        table.getColumns().add(osColumn);
        return table;
    }

    // Create a table view for the Docker containers
    private TableView<DockerContainerObject> createContainerTable() {
        TableView<DockerContainerObject> table = new TableView<>();
        table.setItems(model.getDockerContainers());
        table.autosize();

        TableColumn<DockerContainerObject, String> idColumn = createCopyableColumn("ID", "containerId", 200);

        TableColumn<DockerContainerObject, String> nameColumn = createCopyableColumn("Name", "containerName", 200);

        TableColumn<DockerContainerObject, String> imageColumn = createCopyableColumn("Image", "image", 200);

        TableColumn<DockerContainerObject, Boolean> statusColumn = createStatusColumn();

        TableColumn<DockerContainerObject, String> hostPortColumn = createColumn("Host Port", "hostPort", 100);

        TableColumn<DockerContainerObject, String> exposedPortColumn = createColumn("Exposed Port", "exposedPort", 100);

        TableColumn<DockerContainerObject, Void> toggleColumn = createToggleColumn();

        TableColumn<DockerContainerObject, Void> removeColumn = createRemoveColumn();

        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(imageColumn);
        table.getColumns().add(statusColumn);
        table.getColumns().add(hostPortColumn);
        table.getColumns().add(exposedPortColumn);
        table.getColumns().add(toggleColumn);
        table.getColumns().add(removeColumn);

        return table;
    }

    private <T> TableColumn<T, String> createColumn(String name, String property, int width) {
        TableColumn<T, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }

    private <T> TableColumn<T, String> createCopyableColumn(String name, String property, int width) {
        TableColumn<T, String> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);

        column.setCellFactory(tc -> {
            TableCell<T, String> cell = new TableCell<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem copyMenuItem = new MenuItem("Copy");
            copyMenuItem.setOnAction(e -> {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(cell.getItem());
                clipboard.setContent(content);
            }); //Set the action of the item to copy the cell's content to the clipboard
            contextMenu.getItems().add(copyMenuItem);
            cell.textProperty().bind(cell.itemProperty());
            cell.setContextMenu(contextMenu);
            return cell;
        });

        return column;
    }

    private TableColumn<DockerContainerObject, Void> createRemoveColumn() {
        TableColumn<DockerContainerObject, Void> removeColumn = new TableColumn<>("Remove");
        Callback<TableColumn<DockerContainerObject, Void>, TableCell<DockerContainerObject, Void>> removeCellFactory =
                param -> new TableCell<>() {
                    private final Button action = new Button("Remove");

                    {
                        action.setOnAction(event -> { //Set the action of the button to remove the container
                            DockerContainerObject data = getTableView().getItems().get(getIndex());
                            dockerInterface.removeContainer(data.getContainerId());
                        });
                    }

                    /**
                     * Updates the item in the cell.
                     * If the cell is empty, it sets the graphic to null.
                     * If the cell is not empty, it sets the graphic of the cell to the button.
                     *
                     * @param item - The item to update (not used as this is a Void column).
                     * @param empty - Whether the cell is empty.
                     */
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(action);
                        }
                    }
                };
        removeColumn.setCellFactory(removeCellFactory);
        removeColumn.setPrefWidth(100);
        return removeColumn;
    }

    private TableColumn<DockerContainerObject, Void> createToggleColumn() {
        TableColumn<DockerContainerObject, Void> toggleColumn = new TableColumn<>("Toggle");

        toggleColumn.setCellFactory(param -> new TableCell<>() {
            private final Button action = new Button();

            {
                // Set the action of the button to toggle the container state
                action.setOnAction(event -> toggleContainerState());
            }
            /**
             * Toggles the running state of the Docker container.
             * If the container is running, it stops the container.
             * If the container is not running, it starts the container.
             */
            private void toggleContainerState() {
                DockerContainerObject data = getTableView().getItems().get(getIndex());
                if (data.runningProperty().get()) {
                    dockerInterface.stopContainer(data.getContainerId());
                } else {
                    dockerInterface.startContainer(data.getContainerId());
                }
            }

            /**
             * Updates the item in the cell.
             * If the cell is empty, it sets the graphic to null.
             * If the cell is not empty, it sets the text of the button to "Stop" if the container is running, or "Start" if it's not.
             * It also sets the graphic of the cell to the button.
             *
             * @param item - The item to update (not used as this is a Void column).
             * @param empty - Whether the cell is empty.
             */
            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    DockerContainerObject data = getTableView().getItems().get(getIndex());
                    action.setText(data.runningProperty().get() ? "Stop" : "Start"); // Set the text of the button to "Stop" or "Start" based on the running state
                    setGraphic(action);
                }
            }
        });

        return toggleColumn;
    }
    
    private TableColumn<DockerContainerObject, Boolean> createStatusColumn() {
        TableColumn<DockerContainerObject, Boolean> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().runningProperty()); // Bind the running property to the column

        statusColumn.setCellFactory(column -> new TableCell<>() {
            /**
             * Updates the item in the cell.
             * If the cell is empty, it sets the text to null.
             * If the cell is not empty, it checks the item (running status) and sets the text to "Running" or "Stopped".
             *
             * @param item - The item to update (running status of the Docker container).
             * @param empty - Whether the cell is empty.
             */
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                String text;
                if (empty) {
                    text = null;
                } else {
                    text = item ? "Running" : "Stopped";
                }
                setText(text);
            }
        });
        statusColumn.setPrefWidth(100);
        return statusColumn;
    }

}
