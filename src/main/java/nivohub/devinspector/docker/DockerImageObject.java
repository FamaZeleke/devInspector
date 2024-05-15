package nivohub.devinspector.docker;

public class DockerImageObject {

    private final String imageName;
    private final String[] tags;
    private final String imageId;
    private final String architecture;
    private final String os;
    private final String container;

    public DockerImageObject(String imageId, String imageName, String[] tags, String architecture, String os, String container) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.tags = tags;
        this.architecture = architecture;
        this.os = os;
        this.container = container;
    }

    // Overloaded constructor for default images
    public DockerImageObject(String imageName, String[] tags) {
        this(null, imageName, tags, null, null, null);
    }

   public String getImageId() {
        return imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public String[] getTags() {
        return tags;
    }

    public String getTagsAsString() {
        return String.join(", ", tags);
    }

    public String getArchitecture() {
        return architecture;
    }

    public String getOs() {
        return os;
    }

    public String getContainer() {
        return container;
    }

}