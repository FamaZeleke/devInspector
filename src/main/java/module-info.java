module nivohub.digitalartifactmaven {
    requires javafx.controls;
    requires javafx.fxml;
    requires docker.client;
    requires guava;
    requires com.google.gson;


    opens nivohub.devInspector to javafx.fxml;
    exports nivohub.devInspector;
    exports nivohub.devInspector.view;
    opens nivohub.devInspector.view to javafx.fxml;
}