package nivohub.devinspector.view;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Builder;
import nivohub.devinspector.enums.View;
import nivohub.devinspector.interfaces.ApplicationInterface;
import nivohub.devinspector.interfaces.DockerInterface;
import nivohub.devinspector.model.DockerModel;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

public class MenuBarBuilder implements Builder<Region> {

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
        public Region build() {
                MenuBar menuBar = createMenuBar();
                Node label = dockerConnectionLabel();
                Node switchTheme = toggleThemeSwitch();
                Node progressBox = createProgressBox();

                AnchorPane result = new AnchorPane();
                setAnchors(result, menuBar, progressBox, label, switchTheme);

                return result;
        }

        private void setAnchors(AnchorPane result, MenuBar menuBar, Node progressBox, Node label, Node switchTheme) {
                AnchorPane.setLeftAnchor(menuBar, 0.0);
                AnchorPane.setTopAnchor(menuBar, 0.0);

                AnchorPane.setLeftAnchor(progressBox, 400.0);
                AnchorPane.setTopAnchor(progressBox, 8.0);

                AnchorPane.setRightAnchor(label, 68.0);
                AnchorPane.setTopAnchor(label, 0.0);
                AnchorPane.setBottomAnchor(label, 0.0);

                AnchorPane.setRightAnchor(switchTheme, 0.0);
                AnchorPane.setTopAnchor(switchTheme, 0.0);
                AnchorPane.setBottomAnchor(switchTheme, 0.0);

                result.getChildren().addAll(menuBar, progressBox, label, switchTheme);
        }

        private Node createProgressBox() {
                ProgressBar threadIndicator = new ProgressBar(0);
                threadIndicator.setPrefSize(200,20);
                threadIndicator.progressProperty().bind(Bindings.createDoubleBinding(
                        () -> dockerModel.threadBuildingProperty().get() ? -1d : 0d,
                        dockerModel.threadBuildingProperty()
                ));

                Button stopButton = new Button(null, new FontIcon(Feather.X_SQUARE));
                stopButton.getStyleClass().addAll(
                        Styles.BUTTON_ICON, Styles.BUTTON_OUTLINED, Styles.DANGER, Styles.SMALL
                );
                stopButton.setPrefSize(20, 20);
                stopButton.visibleProperty().bind(dockerModel.threadBuildingProperty());
                stopButton.setOnAction(event -> dockerInterface.stopThread());

                HBox progressBox = new HBox(threadIndicator, stopButton);
                progressBox.setSpacing(10);

                return progressBox;
        }

        private MenuBar createMenuBar() {
                MenuBar menuBar = new MenuBar();
                menuBar.getMenus().addAll(mainMenu(), dockerMenu());
                return menuBar;
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

        private Node dockerConnectionLabel() {
                Label label = new Label("Docker is not running");
                label.textProperty().bind(Bindings.when(dockerModel.dockerConnectedProperty())
                        .then("Docker is running")
                        .otherwise("Docker is not running"));
                label.textFillProperty().bind(Bindings.when(dockerModel.dockerConnectedProperty())
                        .then(javafx.scene.paint.Color.GREEN)
                        .otherwise(javafx.scene.paint.Color.RED));
                return label;
        }

        private Node toggleThemeSwitch() {
                ToggleSwitch toggleSwitch = new ToggleSwitch();
                toggleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> applicationInterface.toggleTheme());
                return toggleSwitch;
        }

}
