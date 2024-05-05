package nivohub.devInspector.model;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class DockerManager {
    private final DockerClient dockerClient;
    private final String platform;

    public DockerManager(User user) {
//        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerTlsVerify(false).build();
//        this.dockerClient = DockerClientBuilder.getInstance(config).build();
        this.platform = user.getPlatform();
        this.dockerClient = createDockerClient();
    }

//    public DockerClient createDockerClient() {
//        String dockerHost = System.getenv("DOCKER_HOST");
//        if (dockerHost == null) {
//            if (platform.equals("windows")) {
//                dockerHost = "npipe:////./pipe/docker_engine";
//            } else {
//                dockerHost = "unix:///var/run/docker.sock";
//            }
//        }
//        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
//                .withDockerHost(dockerHost)
//                .build();
//        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
//                .dockerHost(config.getDockerHost())
//                .sslConfig(config.getSSLConfig())
//                .build();
//        return DockerClientBuilder.getInstance(config)
//                .withDockerHttpClient(httpClient)
//                .build();
//    };

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

    public String createAndRunContainer(String imageName, String tag, int hostPort, int exposedPort) {
        try {
            // Pull the image from Docker Hub
            dockerClient.pullImageCmd(imageName + ":" + tag)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();

            // Setup port configuration
            Ports.Binding binding = Ports.Binding.bindPort(hostPort);
            ExposedPort tcpPort = ExposedPort.tcp(exposedPort);

            HostConfig hostConfig = HostConfig.newHostConfig()
                    .withPortBindings(new PortBinding(binding, tcpPort));

            CreateContainerResponse container = dockerClient.createContainerCmd(imageName + ":" + tag)
                    .withHostConfig(hostConfig)
                    .exec();

            String containerId = container.getId();

            // Start the container
            dockerClient.startContainerCmd(containerId).exec();

            System.out.println("Container started with ID: " + containerId);
            return containerId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public boolean isDockerRunning() {
        try {
            dockerClient.pingCmd().exec();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
