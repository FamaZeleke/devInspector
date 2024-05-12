package nivohub.devinspector.exceptions;

public class DockerNotRunningException extends Exception {
    public DockerNotRunningException(String errorMessage) {
        super("Docker is not running!!!: "+errorMessage);
    }
}
