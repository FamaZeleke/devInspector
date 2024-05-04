package nivohub.devInspector.model;

import nivohub.devInspector.exceptions.FullNameException;
import nivohub.devInspector.exceptions.PasswordException;

public class User {
    private String fullName;

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
}