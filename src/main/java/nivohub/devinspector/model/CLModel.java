package nivohub.devinspector.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CLModel {
    private final ObservableList<String> output = FXCollections.observableArrayList();
    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private final String menuCommand = "java -cp ./target/classes nivohub.devinspector.clitool.CLMain";

    public ObservableList<String> outputProperty() {
        return output;
    }

    public void appendOutput(String newOutput) {
        if (newOutput != null) {
            this.output.add(newOutput);
        }
    }

    public String getMenuCommand() {
        return this.menuCommand;
    }

    public BooleanProperty runningProperty() {
        return running;
    }

}