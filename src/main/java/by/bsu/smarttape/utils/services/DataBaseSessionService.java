package by.bsu.smarttape.utils.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.function.Consumer;

public class DataBaseSessionService {

    private static SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public static Session getSession() {
        return sessionFactory.openSession();
    }

    public static Exception safetyOperation(Consumer<Session> operation) {
        try {
            Session session = getSession();
            Transaction transaction = session.beginTransaction();
            operation.accept(session);
            transaction.commit();
            session.close();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return ex;
        }
        return null;
    }

}
