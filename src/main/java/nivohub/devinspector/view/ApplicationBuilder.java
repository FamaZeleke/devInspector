package nivohub.devinspector.view;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.enums.View;

public class ApplicationBuilder implements Builder<Region> {

    private final ApplicationModel appModel;
    private final MenuBar menu;
    private final Region home;
    private final Region cliView;
    private final Region dockerView;
    // Use user info ^

    public ApplicationBuilder(ApplicationModel model, MenuBar menu, Region home, Region cliView, Region dockerView) {
        this.appModel = model;
        this.menu = menu;
        this.home = home;
        this.cliView = cliView;
        this.dockerView = dockerView;
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
        dockerView.visibleProperty().bind(appModel.currentViewProperty().isEqualTo(View.DOCKER));
        return new StackPane(home, cliView, dockerView);
    }
}
