package nivohub.devInspector.model;

import nivohub.devInspector.exceptions.FullNameException;
import nivohub.devInspector.exceptions.PasswordException;

public class User {
    private String fullName;
    private final String osArch;
    private final String platform;

    public User(String osArch,String platform) {
        this.osArch = osArch;
        this.platform = platform;
    }

    public void setFullName(String fullName) throws FullNameException {
        if (fullName == null || !fullName.contains(" ")) {
            throw new FullNameException("Full name must include at least first and last name");
        }
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void validatePassword(String password) throws PasswordException {
        String storedPassword = "letmein";
        if (!password.equals(storedPassword)) {
            throw new PasswordException("Please enter a valid password! Hint: letmein");
        }
    }

    public String getOsArch() {
        return osArch;
    }

    public String getPlatform() {
        return platform;
    }
}