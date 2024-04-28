package nivohub.devInspector.controller;

import nivohub.devInspector.view.DockerScene;
import nivohub.devInspector.model.DockerManager;

import java.util.Collections;
import java.util.List;

public class DockerController {
    private DockerScene view;
    private DockerManager model;

    public DockerController(DockerScene view, DockerManager model) {
        this.view = view;
        this.model = model;

        populateImageSelection();
        handleRunButtonAction();
    }

    private void populateImageSelection() {
        // Get the list of image names from the model
        List<String> imageNames = model.getImageNames();

        // Add the image names to the image selection dropdown in the view
        view.getImageSelection().getItems().addAll(imageNames);
    }

    private void handleRunButtonAction() {
        // Set the action for the "Run" button in the view
        view.getRunButton().setOnAction(e -> {
            // Get the selected image, port, and tag from the view
            String selectedImage = view.getImageSelection().getSelectionModel().getSelectedItem();
            String port = view.getPortInput().getText();
            String selectedTag = view.getTagSelection().getSelectionModel().getSelectedItem();

            // Call the createAndRunContainers method in the model
            List<String> containerIds = Collections.singletonList(model.createAndRunContainer(selectedImage, port, selectedTag));

            // Display the container IDs in the output area in the view
            for (String containerId : containerIds) {
                view.getOutputArea().getItems().add("Created and started container with ID: " + containerId);
            }
        });
    }
}