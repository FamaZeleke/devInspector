package nivohub.devInspector.model;


import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.ContainerConfig;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
            InputStream is = getClass().getClassLoader().getResourceAsStream("imageDefinitions.json");
            if (is == null) {
                throw new FileNotFoundException("Could not find 'imageDefinitions.json' in resources directory");
            }
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            return new Gson().fromJson(isr, listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image names from JSON file", e);
        }
    }

    public List<String> getImageNames() {
        return imageNames;
    }

    private boolean imageExists(String imageName) {
        try {
            dockerClient.inspectImage(imageName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    public String createAndRunContainer(String imageName, String tag, String port) {
        try {
            dockerClient.pull(imageName + ":" + tag);
            ContainerConfig containerConfig = ContainerConfig.builder()
                .image(imageName + ":" + tag)
                .exposedPorts(port)
                .env("TAG=" + tag)
                .build();
            String containerId = dockerClient.createContainer(containerConfig).id();
            dockerClient.startContainer(containerId);
            return containerId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to pull and run image " + imageName, e);
        }
    }

}
