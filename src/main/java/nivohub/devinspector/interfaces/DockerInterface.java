package nivohub.devinspector.interfaces;

public interface DockerInterface {
      void connectDocker();
      void disconnectDocker();
        void startContainer(String containerId);
        void stopContainer(String containerId);

      void removeContainer(String containerId);
      //If I wanted to grow this application out to include more features, used across my other views, I would add more methods here.
}
