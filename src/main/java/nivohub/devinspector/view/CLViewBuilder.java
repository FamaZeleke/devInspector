package nivohub.devinspector.view;

import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import nivohub.devinspector.model.CLModel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static nivohub.devinspector.controller.CLController.*;

public class CLViewBuilder implements Builder<Region>{

    private final Consumer<String> handleCommand;
    private final CLModel model;
    private final Runnable CLMenu;
    private final Consumer<TaskType> cliTask;

    public CLViewBuilder(CLModel model, Consumer<String> handleCommand, Consumer<TaskType> manageCLI, Runnable runCLMenu) {
        this.handleCommand = handleCommand;
        this.cliTask = manageCLI;
        this.CLMenu = runCLMenu;
        this.model = model;
    }

    @Override
    public Region build() {
        BorderPane result = new BorderPane();
        Region center = createCentre();
        result.setCenter(center);
        result.setLeft(createLeft());
        return result;
    }

    private Region createLeft() {
        VBox result = new VBox(12);
        result.setPadding(new Insets(12));
        result.getChildren().addAll(startButton(), clearButton(), stopButton(), CLMenuButton());
        return result;
    }

    private Node startButton() {
        Button result = styledButton( "Start");
        result.setOnAction(e -> cliTask.accept(TaskType.START));
        return result;
    }

    private Node stopButton() {
        Button result = styledButton( "Stop");
        result.setOnAction(e -> cliTask.accept(TaskType.STOP));
        return result;
    }

    private Node clearButton() {
        Button result = styledButton( "Clear");
        result.setOnAction(e -> cliTask.accept(TaskType.CLEAR));
        return result;
    }

    private Node CLMenuButton() {
        Button result = styledButton( "Run CLI Menu");
        result.setOnAction(e -> CLMenu.run());
        return result;
    }


    private Region createCentre() {
        VBox result = new VBox();
        Node input = createTextField();
        Node output = createOutputArea();

        result.getChildren().addAll(output, input);
        VBox.setVgrow(output, Priority.ALWAYS);
        return result;
    }

    private Node createTextField() {
        TextField input = new TextField();

        input.setPromptText("Enter command here");
        input.setOnAction(e -> {
            handleCommand.accept(input.getText());
            input.clear();
        });
        return input;
    }

    private Node createOutputArea() {
        TextArea output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);
        output.setMaxHeight(Double.MAX_VALUE);
        output.textProperty().bindBidirectional(model.outputProperty());
        model.outputProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                output.selectPositionCaret(output.getLength());
                output.deselect();  // to remove the text selection
            });
        });
        return output;
    }

    private Button styledButton(String label) {
        Button result = new Button(label);
        result.setPrefWidth(100);
        return result;
    }
}

