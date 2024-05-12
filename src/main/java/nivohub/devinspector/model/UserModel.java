package nivohub.devinspector.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserModel {
    private StringProperty fullName = new SimpleStringProperty("");
    private StringProperty inputPassword = new SimpleStringProperty("");
    private BooleanProperty loginFailed = new SimpleBooleanProperty(this, "loginFailed", false);
    private BooleanProperty authenticated = new SimpleBooleanProperty(this, "authorized", false);
    private final String password = "letmein";
    private String platform;
    private String osArch;

    public BooleanProperty loginFailedProperty(){
        return loginFailed;
    }

    public BooleanProperty authenticatedProperty() {
        return authenticated;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty inputPasswordProperty() {
        return inputPassword;
    }

    public String getFullName(){
        return fullName.get();
    }

    public String getPassword() {
        return password;
    }

    public String getInputPassword() {
        return inputPassword.get();
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOsArch() {
        return osArch;
    }

    public String getPlatform() {
        return platform;
    }



//    public void setFullName(String fullName) throws FullNameException {
//        if (fullName == null || !fullName.contains(" ")) {
//            throw new FullNameException("Full name must include at least first and last name");
//        }
//        this.fullName = fullName;
//    }

//    public String getFullName() {
//        return fullName;
//    }
//
//    public void validatePassword(String password) throws PasswordException {
//        String storedPassword = "letmein";
//        if (!password.equals(storedPassword)) {
//            throw new PasswordException("Please enter a valid password! Hint: letmein");
//        }
//    }
//

}