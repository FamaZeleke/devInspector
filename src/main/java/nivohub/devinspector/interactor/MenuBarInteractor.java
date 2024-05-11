package nivohub.devinspector.interactor;

import nivohub.devinspector.model.ApplicationModel;

public class MenuBarInteractor {
    private final ApplicationModel model;

    public MenuBarInteractor(ApplicationModel model) {
        this.model = model;
    }

    public void showHome() {
        model.homeSelectedProperty().set(true);
    }
}
