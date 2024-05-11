package nivohub.devinspector.view;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.model.View;

public class ApplicationBuilder implements Builder<Region> {

    private final ApplicationModel appModel;
    private final MenuBar menu;
    private final Region home;
    private final Region cliView;
    // Use user info ^

    public ApplicationBuilder(ApplicationModel model, MenuBar menu, Region home, Region cliView){
        this.appModel = model;
        this.menu = menu;
        this.home = home;
        this.cliView = cliView;
    }

    @Override
    public Region build() {
        VBox results = new VBox();
        Region view = createViews();
        results.getChildren().addAll(menu,view);
        VBox.setVgrow(view, Priority.ALWAYS);
        return results;
    }

    private Region createViews(){
        home.visibleProperty().bind(appModel.currentViewProperty().isEqualTo(View.HOME));
        cliView.visibleProperty().bind(appModel.currentViewProperty().isEqualTo(View.CLI));
        return new StackPane(home, cliView);
    }
}
