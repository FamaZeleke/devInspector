module nivohub.digitalartifactmaven {
    requires javafx.controls;
    requires javafx.fxml;


    opens nivohub.devInspector to javafx.fxml;
    exports nivohub.devInspector;
}