package nivohub.devInspector.view;

import javafx.scene.Scene;

public abstract class BaseScene {
    protected AppMenu appMenu;

    public BaseScene(AppMenu appMenu) {
        this.appMenu = appMenu;
    }

    public abstract Scene createScene();

    public abstract void setController(Object controller);
}
