package nivohub.devinspector.interactor;


import expectj.ExpectJ;
import expectj.Spawn;
import nivohub.devinspector.model.CLModel;
import nivohub.devinspector.model.UserModel;

public class CLInteractor {

    private final CLModel model;
    private final UserModel userModel;
    private Spawn shell;
    private Thread shellThread;

    public CLInteractor(CLModel model, UserModel userModel) {
        this.model = model;
        this.userModel = userModel;
    }
//todo abstract domain objects to separate package
    public void start() throws Exception {
        // Start a new terminal session
        ExpectJ expectJ = new ExpectJ();
        shellThread = new Thread(() -> {
            try {
                if (System.getProperty("os.name").startsWith("Windows")) {
                    // Windows
                    model.appendOutput("Starting CLI for Windows..."+"\n");
                    shell = expectJ.spawn("cmd.exe");
                } else {
                    // Unix
                    model.appendOutput("Starting CLI for Unix..."+"\n");
                    shell = expectJ.spawn("/bin/bash");
                }
                model.appendOutput("CLI started successfully "+"\n");
            } catch (Exception e) {
                model.appendOutput("Error starting CLI"+ e.getMessage() + "\n");
                e.printStackTrace();
            }
        });
        shellThread.start();
    }

    public void stop() {
        model.appendOutput("""

                Stopping CLI...
                """);
        try {
            shellThread.interrupt();
            shell.stop();
        } catch (Exception e) {
            model.appendOutput("Error stopping CLI"+ "\n" + e.getMessage());
            e.printStackTrace();
        }
        model.appendOutput("""

                CLI stopped successfully
                """);
    }

    public void listenToShellOutput() {
        shellThread = new Thread(() -> {
            try {
                String output;
                String previousOutput = null;
                while (true) {
                    output = shell.getCurrentStandardOutContents();
                    if (output.equals(previousOutput)) {
                        break; // Break the loop if the output has not changed
                    }
                    model.appendOutput(output);
                    previousOutput = output;
                    Thread.sleep(1000); // Wait for a short period of time before checking the output again
                }
            } catch (Exception e) {
                model.appendOutput("Error listening to shell output"+ "\n" + e.getMessage());
                e.printStackTrace();
            }
        });
        shellThread.start();
    }

    public Boolean runCLMenu() {
        String command = model.getMenuCommand();
        listenToShellOutput();
        try {
            model.appendOutput("Executing command: " + command + "\n");
            shell.send(command + "\n");
            return true;
        } catch (Exception e) {
            System.out.println("Error running CLI menu");
            e.printStackTrace();
            return false;
        }
    }

    public Boolean executeCommand(String command) {
        listenToShellOutput();
        model.appendOutput("Executing command: " + command + "\n");
        try {
            shell.send(command + "\n");
        } catch (Exception e) {
            System.out.println("Error executing command: " + command);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void clear() {
        model.appendOutput("Clearing output...\n");
        model.outputProperty().setValue("");
    }
}
