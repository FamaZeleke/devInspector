package nivohub.devInspector.model;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.ContainerConfig;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class DockerManager {
    private DockerClient dockerClient;
    private List<String> imageNames;

    public DockerManager() throws DockerCertificateException {
        this.dockerClient = DefaultDockerClient.fromEnv().build();
        this.imageNames = readImageNames();
    }

    private List<String> readImageNames() {
        Type listType = new TypeToken<List<String>>() {}.getType();
        try {
            return new Gson().fromJson(new FileReader("imageNames.json"), listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image names from JSON file", e);
        }
    }

    public void setupImages() {
        for (String imageName : imageNames) {
            if (!imageExists(imageName)) {
                pullAndRunImage(imageName);
            }
        }
    }

    private boolean imageExists(String imageName) {
        try {
            dockerClient.inspectImage(imageName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void pullAndRunImage(String imageName) {
        try {
            dockerClient.pull(imageName);
            ContainerConfig containerConfig = ContainerConfig.builder().image(imageName).build();
            String containerId = dockerClient.createContainer(containerConfig).id();
            dockerClient.startContainer(containerId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to pull and run image " + imageName, e);
        }
    }


    public void createAndRunContainers() {
    }

    private void createAndRunContainer() {
        // Use the Spotify Docker client to create and run a container from the image
    }

}
