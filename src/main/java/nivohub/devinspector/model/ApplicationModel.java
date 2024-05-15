package nivohub.devinspector.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import nivohub.devinspector.enums.View;

public class ApplicationModel {
    private final ObjectProperty<View> currentView = new SimpleObjectProperty<>(View.HOME);


    public ObjectProperty<View> currentViewProperty() {
        return currentView;
    }

    public void setCurrentView(View view) {
        currentView.set(view);
    }
}
