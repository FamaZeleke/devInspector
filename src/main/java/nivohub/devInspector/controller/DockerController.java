package nivohub.devInspector.controller;

import javafx.application.Platform;
import nivohub.devInspector.view.DockerScene;
import nivohub.devInspector.model.DockerManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class DockerController {
    private final DockerScene view;
    private final DockerManager model;

    public DockerController(DockerScene view, DockerManager model) {
        this.view = view;
        this.model = model;

        populateImageSelection();
        setupImageSelectionListener(view);
        handleRunButtonAction();
    }

    private void setupImageSelectionListener(DockerScene view) {
        view.getImageSelection().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && !oldValue.equals(newValue)) {
                view.getTagSelection().getItems().clear();
            }
            populateTagSelection();
        });
    }

    private void populateImageSelection() {
        // Get the list of image names from the model
        List<String> imageNames = model.getImageNames();

        // Add the image names to the image selection dropdown in the view
        view.getImageSelection().getItems().addAll(imageNames);
    }

    private void populateTagSelection() {
        String selectedImage = view.getImageSelection().getSelectionModel().getSelectedItem();
        if (selectedImage != null) {
            List<String> tags = model.getTags(selectedImage);
            // Add the tags to the tag selection dropdown in the view
            view.getTagSelection().getItems().addAll(tags);
        }
    }

    private void handleRunButtonAction() {
        view.getRunButton().setOnAction(e -> {
            String selectedImage = view.getImageSelection().getSelectionModel().getSelectedItem();
            int hostPort = Integer.parseInt(view.getHostPort().getText());
            int exposedPort = Integer.parseInt(view.getExposedPort().getText());
            String selectedTag = view.getTagSelection().getSelectionModel().getSelectedItem();

            String containerId = model.createAndRunContainer(selectedImage, selectedTag, hostPort, exposedPort);
            if (containerId != null) {
                Platform.runLater(() -> {
                    view.addContainerDetails(containerId, hostPort);
                    view.getOutputArea().getItems().add("Created and started container with ID: " + containerId);
                });
                streamContainerLogs(containerId);
            }
        });
    }

    // Lambda Expression
    private void streamContainerLogs(String containerId) {
        model.streamContainerLogs(containerId, logMessage -> Platform.runLater(() -> view.getOutputArea().getItems().add(logMessage)));
    }


    public void openBrowserToPort(int hostPort) {
        String url = "http://localhost:" + hostPort;
        try {
            // JavaFX Desktop or Java Desktop class can be used to open a browser window
            java.awt.Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
            // Handle exceptions (show an error dialog or log it)
        }
    }





}