package nivohub.devinspector.model;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CLModel {
    private final SimpleStringProperty output = new SimpleStringProperty("");
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

    public String getOutput() {
        return this.output.toString();
    }

    public String getMenuCommand() {
        return this.menuCommand;
    }

}