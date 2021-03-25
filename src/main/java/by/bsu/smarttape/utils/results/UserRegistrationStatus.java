package by.bsu.smarttape.utils.results;

public class UserRegistrationStatus implements RegistrationStatus {

    private final int code;

    private final String message;
    private final String loginMessage;
    private final String passwordMessage;
    private final String emailMessage;

    private UserRegistrationStatus(int code, String message, String loginMessage, String passwordMessage, String emailMessage) {
        this.code = code;
        this.message = message;
        this.loginMessage = loginMessage;
        this.passwordMessage = passwordMessage;
        this.emailMessage = emailMessage;
    }

    public static UserRegistrationStatus createStatus(int code, String message, String loginMessage, String passwordMessage, String emailMessage) {
        return new UserRegistrationStatus(code, message, loginMessage, passwordMessage, emailMessage);
    }

    @Override
    public int getStatus() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLoginMessage() {
        return loginMessage;
    }

    @Override
    public String getEmailMessage() {
        return emailMessage;
    }

    @Override
    public String getPasswordMessage() {
        return passwordMessage;
    }
}
