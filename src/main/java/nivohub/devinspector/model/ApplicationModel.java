package nivohub.devinspector.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ApplicationModel {
    private final BooleanProperty homeSelected = new SimpleBooleanProperty(true);

    public BooleanProperty homeSelectedProperty(){
        return homeSelected;
    }
}
