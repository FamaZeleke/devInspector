package nivohub.devinspector.view;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class HomeViewBuilder implements Builder<Region> {
    @Override
    public Region build() {
        BorderPane results = new BorderPane();
        Label label = new Label("Home");
        results.setTop(label);
        return results;
    }
}
