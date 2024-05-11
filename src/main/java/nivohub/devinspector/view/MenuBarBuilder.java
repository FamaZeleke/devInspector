package nivohub.devinspector.view;

import javafx.scene.control.*;
import javafx.util.Builder;
import nivohub.devinspector.model.ApplicationModel;

import java.util.function.Consumer;

public class MenuBarBuilder implements Builder<MenuBar> {

        private final Consumer<String> navHandler;

        public MenuBarBuilder(Consumer<String> navHandler) {
                this.navHandler = navHandler;
        }


        @Override
        public MenuBar build() {
                Menu mainMenu = new Menu("Main Menu");
                Button cliButton = new Button("Command Line Tools");
//                cliButton.setOnAction(event -> navHandler.runMenuInTerminal());
                CustomMenuItem cliMenuItem = new CustomMenuItem(cliButton);
                cliMenuItem.setHideOnClick(false);

                MenuItem home = new MenuItem("Home");
                home.setOnAction(event -> navHandler.accept("Home"));

                MenuItem docker = new MenuItem("Docker");
//                docker.setOnAction(event -> navHandler.showScene("Docker"));

                MenuItem exit = new MenuItem("Exit");
                exit.setOnAction(event -> System.exit(0));

                mainMenu.getItems().addAll(home, docker, exit, cliMenuItem);

                return new MenuBar(mainMenu);
        }
}
