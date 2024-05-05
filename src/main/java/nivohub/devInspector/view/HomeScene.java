package nivohub.devInspector.view;


import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import nivohub.devInspector.controller.HomeController;
import nivohub.devInspector.model.User;

public class HomeScene extends BaseScene {
    private final User user;
    private final AppMenu appMenu;
    private HomeController controller;

    public HomeScene(User user, AppMenu appMenu) {
        super(appMenu);
        this.appMenu = appMenu;
        this.user = user;
    }


    @Override
    public void setController(Object controller) {
        if (controller instanceof HomeController) {
            this.controller = (HomeController) controller;
        } else {
            throw new IllegalArgumentException("Controller must be a HomeController");
        }
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