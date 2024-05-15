package nivohub.devinspector.view;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Builder;
import nivohub.devinspector.enums.View;
import nivohub.devinspector.interfaces.ApplicationInterface;
import nivohub.devinspector.interfaces.DockerInterface;
import nivohub.devinspector.model.DockerModel;

import java.util.function.Consumer;

public class MenuBarBuilder implements Builder<MenuBar> {

        private final Consumer<View> eventHandler;
        private final DockerInterface dockerInterface;
        private final ApplicationInterface applicationInterface;
        private final DockerModel dockerModel;

        public MenuBarBuilder(Consumer<View> eventHandler, DockerInterface dockerInterface, DockerModel dockerModel, ApplicationInterface applicationInterface) {
                this.eventHandler = eventHandler;
                this.dockerInterface = dockerInterface;
                this.dockerModel = dockerModel;
                this.applicationInterface = applicationInterface;
        }


        @Override
        public MenuBar build() {
                return new MenuBar(mainMenu(), dockerMenu());
        }

        private Menu mainMenu() {
                Menu mainMenu = new Menu("Main Menu");
                MenuItem cliTool = new MenuItem("CLI Tool");
                cliTool.setOnAction(event -> eventHandler.accept(View.CLI));

                MenuItem home = new MenuItem("Home");
                home.setOnAction(event -> eventHandler.accept(View.HOME));

                MenuItem docker = new MenuItem("Docker");
                docker.setOnAction(event -> eventHandler.accept(View.DOCKER));

                Node exit = styledRunnableButton("Exit", applicationInterface::exitApplication);
                CustomMenuItem exitItem = new CustomMenuItem(exit);

                mainMenu.getItems().addAll(home, docker, cliTool, exitItem);
                return mainMenu;
        }

        private Menu dockerMenu() {
                Menu dockerMenu = new Menu("Docker Menu");

                Node connectDockerButton = styledRunnableButton("Connect", dockerInterface::connectDocker);
                connectDockerButton.disableProperty().bind(dockerModel.dockerConnectedProperty());

                Node disconnectDockerButton = styledRunnableButton("Disconnect", dockerInterface::disconnectDocker);
                disconnectDockerButton.disableProperty().bind(dockerModel.dockerConnectedProperty().not());

                CustomMenuItem connectDocker = new CustomMenuItem(connectDockerButton);
                connectDocker.setContent(connectDockerButton);

                CustomMenuItem disconnectDocker = new CustomMenuItem(disconnectDockerButton);
                disconnectDocker.setContent(disconnectDockerButton);

                dockerMenu.getItems().addAll(connectDocker, disconnectDocker);
                return dockerMenu;
        }

        private Node styledRunnableButton(String text, Runnable runnable) {
                Button button = new Button(text);
                button.setOnAction(e -> runnable.run());
                button.setPrefWidth(100);
                return button;
        }


}
