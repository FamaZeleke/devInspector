package nivohub.devInspector.controller;

import nivohub.devInspector.model.DockerCLI;
import nivohub.devInspector.model.VersioningCLI;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Consumer;

public class CommandLineMenu {
    private static final Stack<String> navigationHistory = new Stack<>();
    private static final Map<String, Consumer<Scanner>> optionHandlers = new HashMap<>();

    static {
        optionHandlers.put("mainMenu1", CommandLineMenu::dockerMenu);
        optionHandlers.put("mainMenu2", CommandLineMenu::versionMenu);
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("╔══════════════════════════════╗");
            System.out.println("║             Menu             ║");
            System.out.println("╚══════════════════════════════╝");

            System.out.println("     1. ➤ Option 1: Docker Commands");
            System.out.println("     2. ➤ Option 2: Versioning");
            System.out.println("     3. ➤ Option 3: Exit");
            System.out.println("\nPlease select an option (1-3): ");

            int selection = input.nextInt();
            input.nextLine();

            if (selection < 1 || selection > 3) {
                System.out.println("Invalid selection. Please select a number between 1 and 3.");
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
                    System.out.println("Returning to " + lastOption);
                    optionHandlers.get(lastOption).accept(input);
                } else {
                    System.out.println("No previous option to return to.");
                }
            }
        }
    }


    public static void versionMenu(Scanner input){
        System.out.println("╔═══════════════════════════════╗");
        System.out.println("║        Version commands       ║");
        System.out.println("╚═══════════════════════════════╝");
        System.out.println("     1. ➤ Option 1: JVM Version");
        System.out.println("     2. ➤ Option 2: Node Version");
        System.out.println("     3. ➤ Option 3: Yarn Version");
        System.out.println("     4. ➤ Option 4: Back");
        System.out.println("\nPlease select an option (1-4): ");
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
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║        Docker Commands       ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("     1. ➤ Option 1: Docker PS");
        System.out.println("     2. ➤ Option 2: Docker Images");
        System.out.println("     4. ➤ Option 4: Back");
        System.out.println("\nPlease select an option (1-3): ");
        int selection = input.nextInt();
        input.nextLine();

        String selectedOption = getAndHandleUserInput(selection, 3);
        if (selectedOption != null) {
            System.out.println("Selected option: " + selectedOption);
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
            System.out.println("Invalid selection. Please select a number between 1 and " + optionLimit + ".");
            return null;
        } else {
            if (selection == optionLimit) {
                if (!navigationHistory.isEmpty()) {
                    String lastOption = navigationHistory.pop();
                    System.out.println("Returning to " + lastOption);
                    optionHandlers.get(lastOption);
                } else {
                    System.out.println("No previous option to return to.");
                }
                return null;
            } else {
                return "selectedMenu" + selection;
            }
        }
    }
}

