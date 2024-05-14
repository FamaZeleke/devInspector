package nivohub.devinspector.view;

import javafx.scene.control.*;
import javafx.util.Builder;
import nivohub.devinspector.enums.View;
import nivohub.devinspector.interfaces.DockerInterface;

import java.util.function.Consumer;

public class MenuBarBuilder implements Builder<MenuBar> {

        private final Consumer<View> eventHandler;
        private final DockerInterface dockerInterface;

        public MenuBarBuilder(Consumer<View> eventHandler, DockerInterface dockerInterface) {
                this.eventHandler = eventHandler;
                this.dockerInterface = dockerInterface;
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

                Button exit = new Button("Exit");
                exit.setOnAction(event -> System.exit(0));
                CustomMenuItem exitItem = new CustomMenuItem(exit);

                mainMenu.getItems().addAll(home, docker, cliTool, exitItem);
                return mainMenu;
        }

        private Menu dockerMenu() {
                Menu dockerMenu = new Menu("Docker Menu");

                Button connectDockerButton = new Button("Connect");
                connectDockerButton.setOnAction(e -> dockerInterface.connectDocker());
                CustomMenuItem connectDocker = new CustomMenuItem(connectDockerButton);
                connectDocker.setContent(connectDockerButton);

                Button disconnectDockerButton = new Button("Disconnect");
                disconnectDockerButton.setOnAction(e -> dockerInterface.disconnectDocker());
                CustomMenuItem disconnectDocker = new CustomMenuItem(disconnectDockerButton);
                disconnectDocker.setContent(disconnectDockerButton);

                dockerMenu.getItems().addAll(connectDocker, disconnectDocker);
                return dockerMenu;
        }


}
