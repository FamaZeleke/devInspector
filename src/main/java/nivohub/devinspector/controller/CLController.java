package nivohub.devinspector.controller;

import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import nivohub.devinspector.exceptions.InvalidCommandException;
import nivohub.devinspector.interactor.CLInteractor;
import nivohub.devinspector.model.CLModel;
import nivohub.devinspector.model.UserModel;
import nivohub.devinspector.view.CLViewBuilder;

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
        task.setOnSucceeded(e -> {
            System.out.println("Task completed successfully");
        });
        task.setOnFailed(e -> {
            // Handle error
            e.getSource().getException().printStackTrace();
        });
        new Thread(task).start();
    }

    private void runCLMenu() {
        Task<Void> startCLIMenu = new Task<>() {
            @Override
            protected Void call() throws Exception {
                interactor.runCLMenu();
                return null;
            }
        };
        startCLIMenu.setOnSucceeded(e -> {
            System.out.println("CLI menu started successfully");
        });
        startCLIMenu.setOnFailed(e -> {
            // Handle error
            e.getSource().getException().printStackTrace();
        });
        new Thread(startCLIMenu).start();
    }

    private void handleCommand(String command) {
        Task<Boolean> executeCommand = new Task<>() {
            @Override
            protected Boolean call() throws InvalidCommandException {
                return interactor.executeCommand(command);
            }
        };
        executeCommand.setOnSucceeded(e -> {
            if (Boolean.TRUE.equals(executeCommand.getValue())) {
                System.out.println("Command executed successfully");
            }
        });
        executeCommand.setOnFailed(e -> {
            // Handle error
            e.getSource().getException().printStackTrace();
        });
        new Thread(executeCommand).start();
    }

    public enum TaskType {
        START,
        CLEAR,
        STOP
    }
}


