module nivohub.digitalartifactmaven {
    requires javafx.controls;
    requires javafx.fxml;
    requires docker.client;
    requires guava;
    requires com.google.gson;


    exports nivohub.devInspector;
    exports nivohub.devInspector.view;
    exports nivohub.devInspector.model;
    exports nivohub.devInspector.controller;
    opens nivohub.devInspector to javafx.fxml;
    opens nivohub.devInspector.view to javafx.fxml;
    opens nivohub.devInspector.model to javafx.fxml;
}