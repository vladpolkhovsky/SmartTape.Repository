package by.bsu.smarttape.utils.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Consumer;

public class DataBaseSessionService {

    private static SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    /**
     * Синглтон метод для полученияя сессии.
     * @return Session для работы с БД.
     */

    public static Session getSession() {
        return sessionFactory.openSession();
    }

    /**
     * Безопасный запрос к БД. Под "безопасный" понимается то, что не возникнет "Исключительной ситуации".
     * @param operation - запрос к БД.
     * @return <code>null</code> если запрос был обработан успешно. Иначе <code>Exception</code>, который возник в результате работы.
     */
    public static Exception safetyOperation(Consumer<Session> operation) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            operation.accept(session);
            transaction.commit();
            session.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            transaction.rollback();
            return ex;
        }
        return null;
    }

}
