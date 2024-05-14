package nivohub.devinspector.interactor;

import nivohub.devinspector.model.ApplicationModel;
import nivohub.devinspector.enums.View;

public class MenuBarInteractor {
    private final ApplicationModel model;

    public MenuBarInteractor(ApplicationModel model) {
        this.model = model;
    }

    public void showView(View view) {
        model.setCurrentView(view);
    }
}
