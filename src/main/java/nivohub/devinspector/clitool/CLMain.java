package nivohub.devinspector.clitool;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Consumer;

import static java.lang.System.*;

public class CLMain {
    private static final Stack<String> navigationHistory = new Stack<>();
    private static final Map<String, Consumer<Scanner>> optionHandlers = new HashMap<>();

    static {
        optionHandlers.put("mainMenu1", CLMain::dockerMenu);
        optionHandlers.put("mainMenu2", CLMain::versionMenu);
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(in);
        boolean exit = false;

        while (!exit) {
            out.println("╔══════════════════════════════╗");
            out.println("║             Menu             ║");
            out.println("╚══════════════════════════════╝");

            out.println("     1. ➤ Option 1: Docker Commands");
            out.println("     2. ➤ Option 2: Versioning");
            out.println("     3. ➤ Option 3: Exit");
            out.println("\nPlease select an option (1-3): ");

            int selection = input.nextInt();
            input.nextLine();

            if (selection < 1 || selection > 3) {
                out.println("Invalid selection. Please select a number between 1 and 3.");
                continue;
            }

            if (selection == 3) {
                exit = true;
                continue;
            }

            navigationHistory.push("mainMenu" + selection);
            optionHandlers.get("mainMenu" + selection).accept(input);

            if (input.nextLine().equalsIgnoreCase("back")) {
                if (!navigationHistory.isEmpty()) {
                    String lastOption = navigationHistory.pop();
                    // Call the appropriate method for the last option
                    out.println("Returning to " + lastOption);
                    optionHandlers.get(lastOption).accept(input);
                } else {
                    out.println("No previous option to return to.");
                }
            }
        }
    }


    public static void versionMenu(Scanner input){
        out.println("╔═══════════════════════════════╗");
        out.println("║        Version commands       ║");
        out.println("╚═══════════════════════════════╝");
        out.println("     1. ➤ Option 1: JVM Version");
        out.println("     2. ➤ Option 2: Node Version");
        out.println("     3. ➤ Option 3: Yarn Version");
        out.println("     4. ➤ Option 4: Back");
        out.println("\nPlease select an option (1-4): ");
        int selection = input.nextInt();
        input.nextLine();

        String selectedOption = getAndHandleUserInput(selection, 4);
        if (selectedOption != null) {
            switch (selectedOption){
                case "selectedMenu1":{
                    VersioningCLI.runJavaVersion();
                    break;
                }
                case "selectedMenu2":{
                    VersioningCLI.runNodeVersion();
                    break;
                }
                case "selectedMenu3":{
                    VersioningCLI.runYarnVersion();
                    break;
                }
            }
        }
    }

    public static void dockerMenu(Scanner input) {
        out.println("╔══════════════════════════════╗");
        out.println("║        Docker Commands       ║");
        out.println("╚══════════════════════════════╝");
        out.println("     1. ➤ Option 1: Docker PS");
        out.println("     2. ➤ Option 2: Docker Images");
        out.println("     4. ➤ Option 4: Back");
        out.println("\nPlease select an option (1-3): ");
        int selection = input.nextInt();
        input.nextLine();

        String selectedOption = getAndHandleUserInput(selection, 3);
        if (selectedOption != null) {
            out.println("Selected option: " + selectedOption);
            switch (selectedOption) {
                case "selectedMenu1": {
                    DockerCLI.runDockerCommand("ps");
                    break;
                }
                case "selectedMenu2": {
                    DockerCLI.runDockerCommand("images");
                    break;
                }
            }
        }
    }

    public static String getAndHandleUserInput(int selection, int optionLimit) {
        if (selection < 1 || selection > optionLimit) {
            out.println("Invalid selection. Please select a number between 1 and " + optionLimit + ".");
            return null;
        } else {
            if (selection == optionLimit) {
                if (!navigationHistory.isEmpty()) {
                    String lastOption = navigationHistory.pop();
                    out.println("Returning to " + lastOption);
                    optionHandlers.get(lastOption);
                } else {
                    out.println("No previous option to return to.");
                }
                return null;
            } else {
                return "selectedMenu" + selection;
            }
        }
    }
}

