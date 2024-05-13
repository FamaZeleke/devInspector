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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Consumer;

public class DockerEngine {

    private DockerClient dockerClient;
    private final String platform;

    public DockerEngine(String platform) {
     this.platform = platform;
    }

    public void createDockerClient() {
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

    public boolean isDockerRunning() {
        try {
            dockerClient.pingCmd().exec();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public DockerContainer createAndRunContainer(String imageName, String tag, String containerName, int hostPort, int exposedPort) throws BindingPortAlreadyAllocatedException, InterruptedException {
        pullImage(imageName, tag);
        HostConfig hostConfig = configurePortBindings(hostPort, exposedPort);
        String containerId = createContainer(imageName, tag, containerName, hostConfig);
        startContainer(containerId);

        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(containerId).exec();
        String containerNameFromInfo = containerInfo.getName().substring(1); // Remove leading slash
        String hostPortFromInfo = String.valueOf(hostPort);
        String exposedPortFromInfo = String.valueOf(exposedPort);
        String imageFromInfo = containerInfo.getConfig().getImage();

        return new DockerContainer(containerId, containerNameFromInfo, hostPortFromInfo, exposedPortFromInfo, imageFromInfo, "Running");
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

    public void stopContainer(String containerId) {
        // Stop container
    }

}
