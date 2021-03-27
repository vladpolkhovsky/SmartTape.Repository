package by.bsu.smarttape.utils.services;

import by.bsu.smarttape.models.User;
import by.bsu.smarttape.utils.services.DataBaseSessionService;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

    public static boolean isUserNameRegistered(String userName) {
        return !isFieldUniqInTable("userName", userName);
    }

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
