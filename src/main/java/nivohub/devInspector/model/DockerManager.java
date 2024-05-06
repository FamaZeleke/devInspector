package nivohub.devInspector.model;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.dockerfile.Dockerfile;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nivohub.devInspector.exceptions.BindingPortAlreadyAllocatedException;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The DockerManager class is responsible for managing Docker containers and interacting with the Docker API.
 * It provides methods for creating a Docker client, reading image definitions, retrieving image names and tags,
 * creating and running containers, streaming container logs, and checking if Docker is running.
 */

public class DockerManager {
    private static final String IMAGE_DEFINITIONS_FILE = "imageDefinitions.json";
    private static final Gson GSON_READER = new Gson();
    private final DockerClient dockerClient;
    private final String platform;

    public DockerManager(User user) {
        this.platform = user.getPlatform();
        this.dockerClient = createDockerClient();
    }

    /**
     * Creates a DockerClient based on the platform.
     * returns the DockerClient instance
     */
    public DockerClient createDockerClient() {
        DockerClientConfig config;
        if (platform.equals("windows")) {
            System.out.println("Creating Docker client for Windows platform");
            config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost("npipe:////./pipe/docker_engine")
                    .build();

            DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost())
                    .sslConfig(config.getSSLConfig())
                    .connectionTimeout(Duration.ofSeconds(2))
                    .build();

            return DockerClientBuilder.getInstance(config)
                    .withDockerHttpClient(httpClient)
                    .build();
        } else {
            System.out.println("Creating Docker client for Unix/MacOs platform");
            config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerTlsVerify(false)
                    .build();
            return DockerClientBuilder.getInstance(config).build();
        }
    }

    public boolean isDockerRunning() {
        try {
            dockerClient.pingCmd().exec();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<Map<String, Object>> readImageDefinitions() {
        Type listType = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        try {
            return readFromJsonFile(IMAGE_DEFINITIONS_FILE, listType);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image definitions from JSON file", e);
        }
    }

    private <T> T readFromJsonFile(String fileName, Type typeOfT) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new FileNotFoundException(String.format("Could not find '%s' in resources directory", fileName));
        }
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        return GSON_READER.fromJson(isr, typeOfT);
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

    public String createAndRunContainer(String imageName, String tag, String containerName, int hostPort, int exposedPort) throws BindingPortAlreadyAllocatedException {
        pullImage(imageName, tag);
        HostConfig hostConfig = configurePortBindings(hostPort, exposedPort);
        String containerId = createContainer(imageName, tag, containerName, hostConfig);
        startContainer(containerId);
        return containerId;
    }

    private void pullImage(String imageName, String tag) {
        try {
            dockerClient.pullImageCmd(imageName + ":" + tag)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HostConfig configurePortBindings(int hostPort, int exposedPort) {
        Ports.Binding binding = Ports.Binding.bindPort(hostPort);
        ExposedPort tcpPort = ExposedPort.tcp(exposedPort);

        return HostConfig.newHostConfig()
                .withPortBindings(new PortBinding(binding, tcpPort));
    }

    private String createContainer(String imageName, String tag, String containerName, HostConfig hostConfig) {
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName + ":" + tag)
                .withHostConfig(hostConfig)
                .withName(containerName)
                .exec();
        return container.getId();
    }

    private void startContainer(String containerId) throws BindingPortAlreadyAllocatedException {
        try {
            dockerClient.startContainerCmd(containerId).exec();
        } catch (InternalServerErrorException e) {
            if (e.getMessage().contains("port is already allocated")) {
                throw new BindingPortAlreadyAllocatedException("Port is already allocated" + containerId);
            }
            throw e;
        }
    }

    public void buildImageFromDockerfile(File file) {
        dockerClient.buildImageCmd()
                .withDockerfile(file)
                .exec(new BuildImageResultCallback() {
                    @Override
                    public void onNext(BuildResponseItem item) {
                        super.onNext(item);
                        System.out.println("" + item.getStream());
                    }
                })
                .awaitImageId();
    }

    public void streamContainerLogs(String containerId, Consumer<String> logHandler) {
        try {
            dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withFollowStream(true)
                    .withTailAll()
                    .exec(new ResultCallback.Adapter<Frame>() {
                        @Override
                        public void onNext(Frame frame) {
                            String logMessage = new String(frame.getPayload(), StandardCharsets.UTF_8);
                            logHandler.accept(logMessage);
                        }

                        @Override
                        public void onComplete() {
                            super.onComplete();
                            logHandler.accept("Log streaming completed for container: " + containerId);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            super.onError(throwable);
                            logHandler.accept("Error streaming logs for container: " + containerId + ": " + throwable.getMessage());
                        }
                    });
        } catch (Exception e) {
            logHandler.accept("Failed to start log streaming for container " + containerId + ": " + e.getMessage());
        }
    }
}
