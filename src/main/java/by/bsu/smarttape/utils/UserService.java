package by.bsu.smarttape.utils;

import by.bsu.smarttape.models.User;
import by.bsu.smarttape.utils.services.DataBaseSessionService;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class UserService {

    public static class UserServiceResult {
        public static final int OK = 0;
        public static final int EXCEPTION = 0;

        private int code;
        private Exception ex;
        private Object result;

        private UserServiceResult(int code, Exception ex, Object result) {
            this.code = code;
            this.ex = ex;
            this.result = result;
        }

        public int getCode() {
            return code;
        }

        public Exception getException() {
            return ex;
        }

        public boolean isOk() {
            return getCode() == OK;
        }

        public Object result() {
            return result;
        }

    }

    public static UserServiceResult isUserNameRegistered(String userName) {
        try {
            Session userFindSession = DataBaseSessionService.getSession();
            CriteriaBuilder criteriaBuilder = userFindSession.getCriteriaBuilder();

            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> userRoot = criteriaQuery.from(User.class);
            criteriaQuery.select(userRoot).where(criteriaBuilder.equal(userRoot.get("userName"), userName));

            TypedQuery<User> query = userFindSession.createQuery(criteriaQuery);
            List<User> userList = query.getResultList();

            userFindSession.close();
            return new UserServiceResult(UserServiceResult.OK, null, userList.size() > 0);
        } catch (HibernateException ex) {
            return new UserServiceResult(UserServiceResult.EXCEPTION, ex, null);
        }
    }

    private static UserServiceResult saveNewUser(User user) {
        final User[] savedUser = new User[1];
        Exception exception = DataBaseSessionService.safetyOperation(session -> {
            savedUser[0] = (User) session.save(user);
            System.out.println("saved user id=" + savedUser[0].getId());
        });
        return new UserServiceResult(
                exception == null ? UserServiceResult.OK : UserServiceResult.EXCEPTION,
                exception,
                exception == null ? savedUser[0] : null
        );
    }

}
