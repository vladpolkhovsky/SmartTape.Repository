package by.bsu.smarttape.utils.services;

import by.bsu.smarttape.forms.UserUpdateForm;
import by.bsu.smarttape.models.User;
import by.bsu.smarttape.utils.results.UserUpdateStatus;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class UserService {

    private static boolean isFieldUniqInTable(String fieldName, String value) {
        try {
            Session userFindSession = DataBaseSessionService.getSession();
            CriteriaBuilder criteriaBuilder = userFindSession.getCriteriaBuilder();

            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> userRoot = criteriaQuery.from(User.class);
            criteriaQuery.select(userRoot).where(criteriaBuilder.equal(userRoot.get(fieldName), value));

            TypedQuery<User> query = userFindSession.createQuery(criteriaQuery);
            List<User> userList = query.getResultList();

            userFindSession.close();
            return userList.size() == 0;
        } catch (HibernateException ex) {
            System.err.println(ex);
            return false;
        }
    }


    public static UserUpdateStatus UpdateUser(HttpServletRequest request, UserUpdateForm updateForm) {

        String userNameMessage = null;
        String passwordMessage = null;
        String emailMessage = null;

        User user = ActiveSessionService.getUserBySession(request.getSession());
        System.out.println(request.getSession());

        if (user == null) {
            return new UserUpdateStatus(null, null, null);
        }

        if (updateForm.getUserName() != null) {
            if (
                !updateForm.getUserName().equals(user.getUserName()) &&
                UserService.isUserNameRegistered(updateForm.getUserName())
            )
                userNameMessage = "Имя пользователя уже занято.";
            else if (updateForm.getUserName().length() < 6)
                userNameMessage = "Имя пользователя должно содержать не менее 6 символов.";
        }

        if (updateForm.getPasswordOld() != null || updateForm.getPasswordNew1() != null || updateForm.getPasswordNew2() != null) {
            if (updateForm.getPasswordOld() == null)
                passwordMessage = "Укажите старый пароль.";
            else if (!updateForm.getPasswordOld().equals(user.getPassword()))
                passwordMessage = "Неправильный пароль.";
            else if (
                    (updateForm.getPasswordNew1() != null && !updateForm.getPasswordNew1().equals(updateForm.getPasswordNew2())) ||
                    (updateForm.getPasswordNew2() != null && !updateForm.getPasswordNew2().equals(updateForm.getPasswordNew1()))
            )
                passwordMessage = "Пароли не совпадают.";
            else if (updateForm.getPasswordNew1() == null && updateForm.getPasswordNew2() == null)
                passwordMessage = "Не указан новый пароль.";
            else if (updateForm.getPasswordNew1().length() < 8 && updateForm.getPasswordNew2().length() < 8)
                passwordMessage = "Пароль должен содержать не менее 8 символов.";
        }

        if (
                updateForm.getEmail() != null &&
                !updateForm.getEmail().equals(user.getEmail()) &&
                UserService.isEmailRegistered(updateForm.getEmail())
        )
            emailMessage = "Email уже занят.";

        if (userNameMessage == null && passwordMessage == null && emailMessage == null) {
            Exception exception = DataBaseSessionService.safetyOperation(session -> {
                session.evict(user);
                if (updateForm.getUserName() != null)
                    user.setUserName(updateForm.getUserName());
                if (updateForm.getPasswordNew1() != null)
                    user.setPassword(updateForm.getPasswordNew1());
                if (updateForm.getEmail() != null)
                    user.setEmail(updateForm.getEmail());
                session.update(user);
            });
            if (exception != null)
                return new UserUpdateStatus(null, null, "EXCEPTION: " + exception.getMessage());
            ActiveSessionService.logout(request.getSession());
            ActiveSessionService.createOrUpdateSession(request.getSession(), user);
        }

        return new UserUpdateStatus(userNameMessage, passwordMessage, emailMessage);
    }

    /**
     * Проверка на уникальность имени пользователя.
     * @param userName имя пользоваетеля.
     * @return <code>true</code> если <code>User</code> с таким именем пользователя содержится в БД.
     */

    public static boolean isUserNameRegistered(String userName) {
        return !isFieldUniqInTable("userName", userName);
    }

    public static boolean isEmailRegistered(String email) {
        return !isFieldUniqInTable("email", email);
    }

    /**
     * Результат работы сервиса.
     */

    public static class SaveResult {
        private final boolean saved;
        private final String message;

        private SaveResult(boolean isSaved, String message) {
            this.message = message;
            this.saved = isSaved;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSaved() {
            return saved;
        }
    }

    /**
     * Сохранение в БД.
     * @param user - пользователь, который будет сохранён в БД.
     * @return SaveResult, <code>.isSaved() == true</code> если сохранение прошло успешно. Иначе <code>SaveResult.getMessage</code> содержит информацию об ошибке.
     */

    public static SaveResult saveNewUser(User user) {
        if (!isFieldUniqInTable("userName", user.getUserName()))
            return new SaveResult(false, String.format("Имя пользователя \"%s\" уже используется.", user.getUserName()));

        if (!isFieldUniqInTable("email", user.getEmail()))
            return new SaveResult(false, String.format("Email \"%s\" уже используется.", user.getEmail()));

        Exception exception = DataBaseSessionService.safetyOperation(session -> session.save(user));
        if (exception != null)
            return new SaveResult(false, String.format("Ошибка на стороне сервера: \"%s\".", exception.getMessage()));

        return new SaveResult(true, "OK");
    }

    /**
     * Поиск учётной записи в БД.
     * @param userName имя пользователя
     * @param password пароль пользователя
     * @return объект <code>User</code>, если учётная запись существует. иначе <code>null</code>.
     */

    public static User getUserByUserNameAndPassword(String userName, String password) {
        try {
            Session userFindSession = DataBaseSessionService.getSession();
            CriteriaBuilder criteriaBuilder = userFindSession.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> userRoot = criteriaQuery.from(User.class);
            criteriaQuery.select(userRoot)
                    .where(criteriaBuilder.equal(userRoot.get("userName"), userName))
                    .where(criteriaBuilder.equal(userRoot.get("password"), password));
            TypedQuery<User> query = userFindSession.createQuery(criteriaQuery);
            List<User> userList = query.getResultList();
            userFindSession.close();
            return (userList.size() > 0 ? userList.get(0) : null);
        } catch (HibernateException ex) {
            System.err.println(ex);
            return null;
        }
    }

}
