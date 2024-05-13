package nivohub.devinspector.controller;

import javafx.scene.layout.Region;
import javafx.util.Builder;

public abstract class BaseController {
    protected Builder<Region> viewBuilder;

    public Region getView() {
        return viewBuilder.build();
    }
}
