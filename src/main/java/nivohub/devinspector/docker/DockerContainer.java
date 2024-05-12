package nivohub.devinspector.docker;

public record DockerContainer(
        String containerId,
        String containerName,
        String hostPort,
        String exposedPort,
        String image,
        String status
) {}