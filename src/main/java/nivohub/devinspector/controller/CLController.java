package nivohub.devinspector.controller;

import javafx.concurrent.Task;
import nivohub.devinspector.enums.TaskType;
import nivohub.devinspector.interactor.CLInteractor;
import nivohub.devinspector.model.CLModel;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.CLViewBuilder;

import java.io.IOException;

/**
 * The CommandLineController class represents a controller for running a menu in the terminal.
 * It provides functionality to execute commands in the terminal based on the platform.
 */

public class CLController extends BaseController {

    private final CLInteractor interactor;

    public CLController(UserModel userModel) {
        CLModel model = new CLModel();
        viewBuilder = new CLViewBuilder(model, this::handleCommand, this::manageCLI, this::runCLMenu);
        interactor = new CLInteractor(model, userModel);
    }

    private void manageCLI(TaskType taskType) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                switch (taskType) {
                    case START:
                        interactor.start();
                        break;
                    case STOP:
                        interactor.stop();
                        break;
                    case CLEAR:
                        interactor.clear();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + taskType);
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> interactor.addMessageToOutput("CLI task completed successfully :"+taskType));
        task.setOnFailed(e -> interactor.addMessageToOutput("Failed to complete CLI task: "+taskType+" :"+e.getSource().getException().getMessage()));
        task.run();
    }

    private void runCLMenu() {
        Task<Void> startCLIMenu = new Task<>() {
            @Override
            protected Void call() throws IOException {
                interactor.runCLMenu();
                return null;
            }
        };
        startCLIMenu.setOnSucceeded(e -> interactor.addMessageToOutput("CLI menu started successfully"));
        startCLIMenu.setOnFailed(e -> interactor.addMessageToOutput("Failed to start CLI menu: " + e.getSource().getException().getMessage()));
        new Thread(startCLIMenu).start();
    }

    private void handleCommand(String command) {
        interactor.addMessageToOutput("Executing command: " + command);
        Task<Void> executeCommand = new Task<>() {
            @Override
            protected Void call() throws IOException {
                interactor.executeCommand(command);
                return null;
            }
        };
        executeCommand.setOnSucceeded(e -> interactor.addMessageToOutput("Command executed successfully"));
        executeCommand.setOnFailed(e -> interactor.addMessageToOutput("Failed to execute command: " + e.getSource().getException().getMessage()));
        new Thread(executeCommand).start();
    }

}


