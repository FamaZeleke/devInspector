package nivohub.devinspector.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class CLModel {
    private final SimpleStringProperty output = new SimpleStringProperty("");
    private final BooleanProperty running = new SimpleBooleanProperty(false);
    private final String menuCommand = "java -cp ./target/classes nivohub.devinspector.clitool.CLMain";

    public SimpleStringProperty outputProperty() {
        return output;
    }

    public void appendOutput(String newOutput) {
        if (newOutput != null) {
            String currentOutput = this.output.get();
            this.output.setValue(currentOutput+newOutput);
        }
    }

    public String getMenuCommand() {
        return this.menuCommand;
    }

    public BooleanProperty runningProperty() {
        return running;
    }

}