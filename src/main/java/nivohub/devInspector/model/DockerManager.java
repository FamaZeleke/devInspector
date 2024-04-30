package nivohub.devInspector.model;



//import com.github.dockerjava.api.DockerClient;
//import com.github.dockerjava.api.command.CreateContainerResponse;
//import com.github.dockerjava.api.command.PullImageResultCallback;
//import com.github.dockerjava.api.model.ExposedPort;
//import com.github.dockerjava.api.model.Ports;
//import com.github.dockerjava.core.DefaultDockerClientConfig;
//import com.github.dockerjava.core.DockerClientBuilder;
//import com.github.dockerjava.core.DockerClientConfig;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class DockerManager {
    private List<Map<String, Object>> imageNames;
    private List<String> tags;
    private final DockerClient dockerClient;
    private DefaultDockerClientConfig config;

    public DockerManager() {
        this.imageNames = readImageDefinitions();
        this.config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerTlsVerify(false).build();
        this.dockerClient = DockerClientBuilder.getInstance(config).build();
    }

    private List<Map<String, Object>> readImageDefinitions() {
        Type listType = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
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

    public String createAndRunContainer(String imageName, String tag, int port) {
        try {
            // Pull the image from Docker Hub
            dockerClient.pullImageCmd(imageName + ":" + tag)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();

            // Setup port configuration
            ExposedPort tcpPort = ExposedPort.tcp(port);
            Ports portBindings = new Ports();
            portBindings.bind(tcpPort, Ports.Binding.bindPort(port));

            // Create container
            CreateContainerResponse container = dockerClient.createContainerCmd(imageName + ":" + tag)
                    .withExposedPorts(tcpPort)
                    .withPortBindings(portBindings)
                    .exec();

            // Start the container
            dockerClient.startContainerCmd(container.getId()).exec();

            System.out.println("Container started with ID: " + container.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Container started";
    }

}
