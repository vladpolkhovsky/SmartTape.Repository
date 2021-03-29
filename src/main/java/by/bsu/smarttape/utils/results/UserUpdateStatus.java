package by.bsu.smarttape.utils.results;

public class UserUpdateStatus {

    private final String userNameStatus;
    private final String passwordStatus;
    private final String emailStatus;

    public UserUpdateStatus(String userNameStatus, String passwordStatus, String emailStatus) {
        this.userNameStatus = userNameStatus;
        this.passwordStatus = passwordStatus;
        this.emailStatus = emailStatus;
    }

    public boolean isOk() {
        return emailStatus == null && userNameStatus == null && passwordStatus == null;
    }

    public String getEmailStatus() {
        return emailStatus;
    }

    public String getUserNameStatus() {
        return userNameStatus;
    }

    public String getPasswordStatus() {
        return passwordStatus;
    }

}
