package by.bsu.smarttape.forms;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class UserRegistrationForm {

    private final static String USER_NAME = "user_name";
    private final static String PASSWORD = "password";
    private final static String EMAIL = "password";

    private String userName;
    private String password;
    private String email;

    public UserRegistrationForm(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Optional<String[]> userName = Optional.ofNullable(map.get(USER_NAME));
        Optional<String[]> password = Optional.ofNullable(map.get(PASSWORD));
        Optional<String[]> email = Optional.ofNullable(map.get(EMAIL));
        this.userName = userName.orElse(new String[]{ null })[0];
        this.password = password.orElse(new String[]{ null })[0];
        this.email = email.orElse(new String[]{ null })[0];
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
