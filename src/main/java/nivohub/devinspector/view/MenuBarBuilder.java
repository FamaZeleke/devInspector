package nivohub.devinspector.view;

import javafx.scene.control.*;
import javafx.util.Builder;
import nivohub.devinspector.model.View;

import java.util.function.Consumer;

public class MenuBarBuilder implements Builder<MenuBar> {

        private final Consumer<View> eventHandler;

        public MenuBarBuilder(Consumer<View> eventHandler) {
                this.eventHandler = eventHandler;
        }


        @Override
        public MenuBar build() {
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

                return new MenuBar(mainMenu);
        }
}
