package nivohub.devinspector.exceptions;

public class DockerNotRunningException extends Exception {
    public DockerNotRunningException() {
        super("Docker is not running!!!");
    }
}
