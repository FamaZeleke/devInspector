package nivohub.devInspector;

import nivohub.devInspector.exceptions.FullNameException;
import nivohub.devInspector.exceptions.PasswordException;

public class User {
    private String fullName;
    private final String password = "letmein";

    public void setFullName(String fullName) throws FullNameException {
        if (fullName == null || !fullName.contains(" ")) {
            throw new FullNameException("Full name must include at least first and last name");
        }
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean validatePassword(String password) throws PasswordException {
        if (!password.equals(this.password)) {
            throw new PasswordException("Please enter a valid password!");
        }
        return true;
    }
}