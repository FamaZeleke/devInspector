package nivohub.devInspector.controller;

import java.io.IOException;

/**
 * The CommandLineController class represents a controller for running a menu in the terminal.
 * It provides functionality to execute commands in the terminal based on the platform.
 */
public class CommandLineController {
    private static String platform;

    public CommandLineController(String platform) {
        CommandLineController.platform = platform;
    }


    public void runMenuInTerminal() {
        String projectRootPath = System.getProperty("user.dir");
        String classpath;
        if (projectRootPath.endsWith("/Contents")) {
            // The application is running from the .app bundle
            classpath = projectRootPath + "/Java/devInspector-1.0-jar-with-dependencies.jar";
        } else {
            // The application is not running from the .app bundle
            classpath = projectRootPath + "/target/classes";
        }

        String runCommand = "java -cp " + classpath + " nivohub.devInspector.controller.CommandLineMenu";
        String terminalCommand;

        System.out.println("Running menu in terminal for platform: " + platform);
        try {
            if ("mac".contains(platform)) {
                terminalCommand = "osascript -e 'tell app \"Terminal\" to do script \"" + runCommand + "\"'";
                Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", terminalCommand });
            } else if ("windows".contains(platform)){
                terminalCommand = "cmd.exe /c start cmd.exe /k \"" + runCommand + "\"";
                Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", terminalCommand });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
