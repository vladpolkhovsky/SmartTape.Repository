package by.bsu.smarttape.forms;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

public class UserUpdateForm {

    private final static String USER_NAME = "user-name";
    private final static String PASSWORD_OLD = "password-old";
    private final static String PASSWORD_NEW_1 = "password-new-1";
    private final static String PASSWORD_NEW_2 = "password-new-2";
    private final static String EMAIL = "email";

    private final String userName;
    private final String passwordOld;
    private final String passwordNew1;
    private final String passwordNew2;
    private final String email;

    public UserUpdateForm(HttpServletRequest request) {
        Map<String, String[]> map = request.getParameterMap();
        Optional<String[]> userName = Optional.ofNullable(map.get(USER_NAME));
        Optional<String[]> passwordOld = Optional.ofNullable(map.get(PASSWORD_OLD));
        Optional<String[]> passwordNew1 = Optional.ofNullable(map.get(PASSWORD_NEW_1));
        Optional<String[]> passwordNew2 = Optional.ofNullable(map.get(PASSWORD_NEW_2));
        Optional<String[]> email = Optional.ofNullable(map.get(EMAIL));
        this.userName = userName.map(strings -> (strings[0].length() == 0 ? null : strings)).orElse(new String[]{ null })[0];
        this.passwordOld = passwordOld.map(strings -> (strings[0].length() == 0 ? null : strings)).orElse(new String[]{ null })[0];
        this.passwordNew1 = passwordNew1.map(strings -> (strings[0].length() == 0 ? null : strings)).orElse(new String[]{ null })[0];
        this.passwordNew2 = passwordNew2.map(strings -> (strings[0].length() == 0 ? null : strings)).orElse(new String[]{ null })[0];
        this.email = email.map(strings -> (strings[0].length() == 0 ? null : strings)).orElse(new String[]{ null })[0];
    }

    public String getUserName() {
        return userName;
    }

    public String getPasswordOld() {
        return passwordOld;
    }

    public String getPasswordNew1() {
        return passwordNew1;
    }

    public String getPasswordNew2() {
        return passwordNew2;
    }

    public String getEmail() {
        return email;
    }
}
