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
import java.util.Map;

public class DockerManager {
    private DockerClient dockerClient;
    private List<Map<String, Object>> imageNames;
    private List<String> tags;

    public DockerManager() throws DockerCertificateException {
        this.dockerClient = DefaultDockerClient.fromEnv().build();
        this.imageNames = readImageDefinitions();
    }


    private List<Map<String, Object>> readImageDefinitions() {
        Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("imageDefinitions.json");
            if (is == null) {
                throw new FileNotFoundException("Could not find 'imageDefinitions.json' in resources directory");
            }
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            return new Gson().fromJson(isr, listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image definitions from JSON file", e);
        }
    }

    public List<String> getImageNames() {
        List<Map<String, Object>> imageDefinitions = readImageDefinitions();
        List<String> imageNames = new ArrayList<>();
        for (Map<String, Object> imageDefinition : imageDefinitions) {
            imageNames.add((String) imageDefinition.get("image"));
        }
        return imageNames;
    }

    public List<String> getTags(String imageName) {
        List<Map<String, Object>> imageDefinitions = readImageDefinitions();
        for (Map<String, Object> imageDefinition : imageDefinitions) {
            if (imageName.equals(imageDefinition.get("image"))) {
                return (List<String>) imageDefinition.get("tags");
            }
        }
        return new ArrayList<>();
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
