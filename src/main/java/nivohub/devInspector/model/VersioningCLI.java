package nivohub.devInspector.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class VersioningCLI {
    public static void runJavaVersion() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("java", "--version");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            List<String> versions = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                versions.add(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void runNodeVersion() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("node", "--version");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            List<String> versions = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                versions.add(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void runYarnVersion() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("yarn", "--version");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            List<String> versions = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                versions.add(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

