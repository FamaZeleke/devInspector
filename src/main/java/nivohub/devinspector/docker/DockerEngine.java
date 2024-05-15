package nivohub.devinspector.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import nivohub.devinspector.exceptions.BindingPortAlreadyAllocatedException;
import nivohub.devinspector.exceptions.DockerNotRunningException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

//Domain class - DockerEngine
public class DockerEngine {
    private DockerClient dockerClient;
    private final String platform;

    public DockerEngine(String platform) {
     this.platform = platform;
    }

    // Docker client operations
    public void createDockerClient() throws DockerNotRunningException {
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

             dockerClient = DockerClientBuilder.getInstance(config)
                    .withDockerHttpClient(httpClient)
                    .build();
        } else {
            System.out.println("Creating Docker client for Unix/MacOs platform");
            config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerTlsVerify(false)
                    .build();
            dockerClient = DockerClientBuilder.getInstance(config).build();
        }
        isDockerRunning();
    }

    public void closeDockerClient() throws IOException {
        dockerClient.close();
    }

    public void isDockerRunning() throws DockerNotRunningException {
        try {
            dockerClient.pingCmd().exec();
        } catch (Exception e) {
            throw new DockerNotRunningException(e.getMessage());
        }
    }


    // Container Build Processes
    public String pullAndRunContainer(String imageName, String tag, String containerName, int hostPort, int exposedPort) throws BindingPortAlreadyAllocatedException, InterruptedException {
        // Pull the Docker image from the Docker Hub
        pullImage(imageName, tag);
        // Configure port bindings
        HostConfig hostConfig = configurePortBindings(hostPort, exposedPort);

        // Create and run the Docker container from the pulled image
        String containerId = createContainer(imageName, tag, containerName, hostConfig);
        startContainer(containerId);
        return containerId;
    }

    public String buildAndRunContainerFromDockerfile(File dockerfile, String containerName, int hostPort, int exposedPort) throws BindingPortAlreadyAllocatedException {
        // Build the Docker image from the Dockerfile
        String imageId = buildImage(dockerfile);

        // Configure port bindings
        HostConfig hostConfig = configurePortBindings(hostPort, exposedPort);

        // Create and run the Docker container from the built image
        String containerId = createContainer(imageId, null, containerName, hostConfig);
        startContainer(containerId);
        return containerId;
    }

    private void pullImage(String imageName, String tag) throws InterruptedException {
        try {
            dockerClient.pullImageCmd(imageName + ":" + tag)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();
        } catch (InterruptedException e) {
            throw new InterruptedException(e.getMessage());
        }
    }

    private String buildImage(File dockerfile) {
        return dockerClient.buildImageCmd()
                .withDockerfile(dockerfile)
                .exec(new BuildImageResultCallback())
                .awaitImageId();
    }

    private HostConfig configurePortBindings(int hostPort, int exposedPort) {
        Ports.Binding binding = Ports.Binding.bindPort(hostPort);
        ExposedPort tcpPort = ExposedPort.tcp(exposedPort);

        return HostConfig.newHostConfig()
                .withPortBindings(new PortBinding(binding, tcpPort));
    }

    //TODO overload

    private String createContainer(String imageName, String containerName) {
        return createContainer(imageName, null, containerName, null);
    }

    private String createContainer(String imageName, String tag, String containerName) {
        return createContainer(imageName, tag, containerName, null);
    }

    private String createContainer(String imageName, String tag, String containerName, HostConfig hostConfig) {
        CreateContainerResponse container = dockerClient.createContainerCmd(tag != null? imageName + ":" + tag : imageName)
                .withHostConfig(hostConfig != null ? hostConfig : HostConfig.newHostConfig())
                .withName(containerName)
                .exec();
        return container.getId();
    }


    // Container control operations
    public InspectContainerResponse getContainerInfo(String containerId) {
        return dockerClient.inspectContainerCmd(containerId).exec();
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

    public void startContainer(String containerId) throws BindingPortAlreadyAllocatedException {
        try {
            dockerClient.startContainerCmd(containerId).exec();
        } catch (InternalServerErrorException e) {
            if (e.getMessage().contains("port is already allocated")) {
                throw new BindingPortAlreadyAllocatedException("Port is already allocated" + containerId);
            }
            throw e;
        }
    }

    public void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
    }

    public void removeContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }

    public void removeImage(String imageId) {
        dockerClient.removeImageCmd(imageId).exec();
    }

    public List<Container> listContainers() {
        return dockerClient.listContainersCmd().exec();
    }

    public List<Image> listImages() {
        return dockerClient.listImagesCmd().exec();
    }

}
