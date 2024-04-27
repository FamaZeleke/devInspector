package nivohub.devInspector;


import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import nivohub.devInspector.view.AppMenu;

public class Home {
    private final User user;
    private final AppMenu appMenu;

    public Home(User user, AppMenu appMenu) {
        this.user = user;
        this.appMenu = appMenu;
    }

    public Scene createScene() {
        MenuBar menuBar = appMenu.createMenu();
        Label welcomeLabel = new Label("Welcome " + user.getFullName());
        welcomeLabel.setFont(Font.font("Inter", FontWeight.BOLD, 42));
        welcomeLabel.setAlignment(Pos.TOP_CENTER);

        GridPane appGrid = new GridPane();
        appGrid.add(welcomeLabel, 1, 0);

        VBox vBox = new VBox(menuBar, appGrid);

        return new Scene(vBox, 960, 600);
    }
}