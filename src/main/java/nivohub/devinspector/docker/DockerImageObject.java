package nivohub.devinspector.docker;

public class DockerImageObject {

private final String imageName;
private final String[] tags;

    public DockerImageObject(String imageName, String[] tags) {
        this.imageName = imageName;
        this.tags = tags;
    }

    public String imageName() {
        return imageName;
    }

    public String[] tags() {
        return tags;
    }

}