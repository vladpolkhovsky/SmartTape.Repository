package by.bsu.smarttape.utils.results;

public class UserRegistrationStatus implements SimpleStatus {

    private final int code;

    private final String message;

    private UserRegistrationStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static UserRegistrationStatus createStatus(int code, String message) {
        return new UserRegistrationStatus(code, message);
    }

    @Override
    public int getStatus() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
