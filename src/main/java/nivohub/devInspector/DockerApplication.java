package nivohub.devInspector;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import nivohub.devInspector.view.AppMenu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class DockerApplication {
    private AppMenu appMenu;

    public DockerApplication(AppMenu appMenu) {
        this.appMenu = appMenu;
    }
    public Scene createScene() {
        ListView<String> outputArea = new ListView<>();
        outputArea.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("is running")) {
                        setStyle("-fx-text-fill: green;");
                    } else if (item.contains("is not running")) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        Button dockerPsButton = new Button("Validate Running Containers");
        dockerPsButton.setOnAction(event -> runCommand(outputArea, "docker", "ps"));

        Button dockerImagesButton = new Button("Validate Running Images");
        dockerImagesButton.setOnAction(event -> runCommand(outputArea, "docker", "images"));

        MenuBar menuBar = appMenu.createMenu();

        HBox hbox = new HBox(dockerPsButton, dockerImagesButton);
        VBox vbox = new VBox(menuBar, hbox, outputArea);

        return new Scene(vbox, 800, 600);
    }

    private void runCommand(ListView<String> outputArea, String... command) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(command);
                    Process process = processBuilder.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    ObservableList<String> runningContainers = FXCollections.observableArrayList();
                    while ((line = reader.readLine()) != null) {
                        runningContainers.add(line);
                    }

                    checkRunningContainers(outputArea, runningContainers);

                    int exitCode = process.waitFor();
                    Platform.runLater(() -> outputArea.getItems().add("Command exited with code: " + exitCode));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    private void checkRunningContainers(ListView<String> outputArea, ObservableList<String> runningContainers) {
        List<String> aliases = Arrays.asList("kafka", "redis", "localstack-aws", "core-db", "nivo-db", "institutions-db", "redshift", "nginx", "chaser", "identity", "document", "provider", "bank-account", "sdk-consumer", "deep-links", "bms", "open-banking", "logo-server");
        for (String alias : aliases) {
            boolean isRunning = false;
            for (String container : runningContainers) {
                String[] components = container.split("\\s+");
                String name = components[components.length - 1];
                if (name.contains(alias)) {
                    isRunning = true;
                    break;
                }
            }
            String containerName = "nivo-" + alias + "-1";
            if (isRunning) {
                // TODO Usually Inject things as opposed to global state
                // TODO Juice
                Platform.runLater(() -> outputArea.getItems().add(containerName + " is running \u2713"));
            } else {
                Platform.runLater(() -> outputArea.getItems().add(containerName + " is not running \u2717"));
            }
        }
    }
}