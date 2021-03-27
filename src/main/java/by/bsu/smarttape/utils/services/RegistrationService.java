package by.bsu.smarttape.utils.services;

import by.bsu.smarttape.forms.UserRegistrationForm;
import by.bsu.smarttape.models.User;
import by.bsu.smarttape.utils.results.SimpleStatus;
import by.bsu.smarttape.utils.results.UserRegistrationStatus;

public class RegistrationService {

    private static class FieldStatus {
        private boolean correct;
        private String message;

        public FieldStatus(boolean correct, String message) {
            this.correct = correct;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public boolean isCorrect() {
            return correct;
        }
    }

    private static UserRegistrationStatus check(String email, String userName, String password) {
        FieldStatus emailStatus = checkEmail(email);
        FieldStatus userNameStatus = checkUserName(userName);
        FieldStatus passwordStatus = checkPassword(password);
        int code = emailStatus.isCorrect() && userNameStatus.isCorrect() && passwordStatus.isCorrect() ?
                UserRegistrationStatus.OK : UserRegistrationStatus.ERROR;
        String generalMessage = (code == UserRegistrationStatus.OK ?
                "Регистрация прошла успешно." : "Некоторые поля зполнены неверно.");
        return UserRegistrationStatus.createStatus(code, generalMessage, userNameStatus.getMessage(), passwordStatus.getMessage(), emailStatus.getMessage());
    }

    private static FieldStatus checkPassword(String password) {
        if (password == null || password.length() < 8)
            return new FieldStatus(false, "Имя пользователя должно содержать не менее 8 символов.");
        return new FieldStatus(true, "OK");
    }

    private static FieldStatus checkUserName(String userName) {
        if (userName == null || userName.length() < 6)
            return new FieldStatus(false, "Имя пользователя должно содержать не менее 6 символов.");
        boolean isUserNameAlreadyUsed = UserService.isUserNameRegistered(userName);
        if (isUserNameAlreadyUsed)
            return new FieldStatus(false, "Имя пользователя \'" + userName + "\' уже занято.");
        return new FieldStatus(true, "OK");
    }

    private static FieldStatus checkEmail(String email) {
        if (email == null)
            return new FieldStatus(false, "Поле не заполнено");
        return new FieldStatus(true, "OK");
    }

    /**
     * Регистрация пользователя.
     * @param registrationForm - объект формы регистрации пользователя
     * @return статус регистрации с инфорамацией об ошибке или успехе.
     */

    public static UserRegistrationStatus register(UserRegistrationForm registrationForm) {
       UserRegistrationStatus status = check(registrationForm.getEmail(), registrationForm.getUserName(), registrationForm.getPassword());
       if (status.getStatus() == SimpleStatus.OK) {
           UserService.SaveResult saveResult = UserService.saveNewUser(new User(registrationForm.getUserName(), registrationForm.getEmail(), registrationForm.getPassword()));
           if (!saveResult.isSaved())
               return UserRegistrationStatus.createStatus(
                       SimpleStatus.ERROR,
                       "Ошибка \'" + saveResult.getMessage() + "\'.",
                       status.getLoginMessage(),
                       status.getPasswordMessage(),
                       status.getEmailMessage()
               );
       }
       return status;
    }

}
