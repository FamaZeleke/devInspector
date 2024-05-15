package nivohub.devinspector.interactor;



import javafx.application.Platform;
import nivohub.devinspector.clitool.ShellCommandExecutor;
import nivohub.devinspector.model.CLModel;
import nivohub.devinspector.model.UserModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CLInteractor {

    private final CLModel model;
    private ShellCommandExecutor shellCommandExecutor;
    private Thread shellThread;
    private String platform;

    public CLInteractor(CLModel model, UserModel userModel) {
        this.model = model;
        this.platform = userModel.getPlatform();
    }

    public void start() {
        shellCommandExecutor = new ShellCommandExecutor();
        shellThread = new Thread(() -> {
            try {
                if (platform.contains("win")) {
                    // Windows
                 addMessageToOutput("Starting CLI for Windows...");
                    shellCommandExecutor.executeCommand("cmd.exe");
                } else {
                    // Unix
                 addMessageToOutput("Starting CLI for Unix...");
                    shellCommandExecutor.executeCommand("/bin/bash");
                }
                addMessageToOutput("CLI started successfully ");
                model.runningProperty().set(true);
                listenToShellOutput(); // Start listening to shell output
            } catch (Exception e) {
             addMessageToOutput("Error starting CLI"+ e.getMessage());
                e.printStackTrace();
            }
        });
        shellThread.start();
    }

    public void stop() {
        addMessageToOutput("Stopping CLI...");
        try {
            shellThread.interrupt();
        } catch (Exception e) {
            addMessageToOutput("Error stopping CLI"+ e.getMessage());
            e.printStackTrace();
        }
        addMessageToOutput("CLI stopped successfully");
        model.runningProperty().set(false);
    }

    public void listenToShellOutput() {
        Thread thread = new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(shellCommandExecutor.getOutputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    final String outputLine = line;
                 model.outputProperty().add(outputLine);
                }
            } catch (IOException e) {
             model.outputProperty().add("Error listening to shell output" + "\n" + e.getMessage());
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void runCLMenu() {
        String projectRootPath = System.getProperty("user.dir");
        addMessageToOutput("Project's root directory is: " + projectRootPath);

        String cdCommand = "cd " + projectRootPath;
        String runCommand = model.getMenuCommand();
        String terminalCommand;

        addMessageToOutput("Running menu in terminal for platform: " + platform);
        try {
            if ("mac".contains(platform)) {
                terminalCommand = "osascript -e 'tell app \"Terminal\" to do script \"" + cdCommand + " && " + runCommand + "\"'";
                Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", terminalCommand });
            } else if ("windows".contains(platform)){
                terminalCommand = "cmd.exe /c start cmd.exe /k \"cd " + projectRootPath + " && " + runCommand + "\"";
                Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", terminalCommand });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeCommand(String command) throws IOException {
        shellCommandExecutor.executeCommand(command);
    }

    public void addMessageToOutput(String message) {
        Platform.runLater(() -> model.appendOutput(message));
    }

    public void clear() {
        addMessageToOutput("Clearing output...");
        model.outputProperty().clear();
    }
}
