package nivohub.devInspector.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserModel {
    private StringProperty fullName = new SimpleStringProperty("");
    private StringProperty password = new SimpleStringProperty("");
    private final BooleanProperty passwordValid = new SimpleBooleanProperty(false);
    private BooleanProperty loginFailed = new SimpleBooleanProperty(this, "loginFailed", false);
    private final String validPassword = "letmein";
//    private final String osArch;
//    private final String platform;

//    public User(String osArch,String platform) {
//        this.osArch = osArch;
//        this.platform = platform;
//    }

    public BooleanProperty loginFailedProperty(){
        return loginFailed;
    }

    public BooleanProperty passwordValidProperty(){
        return passwordValid;
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public StringProperty passwordProperty(){
        return password;
    }

    public void setPasswordValid(Boolean isValid){
        this.passwordValid.set(isValid);
    }

    public void setPassword(String password){
        this.password.set(password);
    }

    public void setFullName(String fullName){
        this.fullName.set(fullName);
    }

    public Boolean getPasswordValid(){
        return passwordValid.get();
    }

    public String getFullName(){
        return fullName.get();
    }

    public String getPassword(){
        return password.get();
    }

    public String getValidPassword() {
        return validPassword;
    }

    public String getPlatform() {
        return "";
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
//    public String getOsArch() {
//        return osArch;
//    }
//
//    public String getPlatform() {
//        return platform;
//    }
}