package nivohub.devinspector.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DockerCLI {
    public static void main(String[] args) {
        runDockerCommand("ps");
        runDockerCommand("images");
    }

    public static void runDockerCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("docker", command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
            System.out.println("Press 3 to go back to the main menu");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}


