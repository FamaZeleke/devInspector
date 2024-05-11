package nivohub.devinspector.view;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.model.UserModel;

public class ApplicationViewBuilder implements Builder<Region> {

    private final ApplicationModel appModel;
    private final MenuBar menu;
    private final Region home;
    private final UserModel userModel;

    public ApplicationViewBuilder(ApplicationModel model, UserModel userModel, MenuBar menu, Region home){
        this.appModel = model;
        this.userModel = userModel;
        this.menu = menu;
        this.home = home;
    }

    @Override
    public Region build() {
        VBox results = new VBox();
        results.getChildren().addAll(menu, createViews());
        return results;
    }

    private Region createViews(){
        home.visibleProperty().bind(appModel.homeSelectedProperty());
        return new StackPane(home);
    }
}
