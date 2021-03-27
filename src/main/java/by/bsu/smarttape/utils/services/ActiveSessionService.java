package by.bsu.smarttape.utils.services;

import by.bsu.smarttape.models.User;

import javax.servlet.http.HttpSession;
import java.util.*;


// TODO РЕАЛИЗОВАТЬ СТАБИЛЬНУЮ РАБОТУ С НЕСКОЛЬКИМИ ОБРАЩЕНИЯМИ. МНОГОПОТОЧНОСТЬ. БЛОКИРОВКУ.
public class ActiveSessionService {

    private static final long SESSION_LIVE_TIME = 900000;

    private static final Map<String, UserSessionWrapper> activeSession = initService();

    private static Timer cleaner;

    private static volatile boolean isLocked = false;

    private static Map<String, UserSessionWrapper> initService() {
        cleaner = new Timer();

        cleaner.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        isLocked = true;

                        isLocked = false;
                    }
                },
                SESSION_LIVE_TIME / 4
        );

        return new HashMap<>();
    }

    private static class UserSessionWrapper {
        User user;
        long sessionStart;

        public UserSessionWrapper(User user) {
            this.sessionStart = System.currentTimeMillis();
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        public long getSessionStart() {
            return sessionStart;
        }

        public void setSessionStart(long sessionStart) {
            this.sessionStart = sessionStart;
        }
    }

    private static String getShortHash(HttpSession session) {
        return session.toString().substring(session.toString().indexOf("@") + 1);
    }

    private static final UserSessionWrapper nullWrapper = new UserSessionWrapper(null);

    //debug
    public static Set<String> getSessions() {
        return activeSession.keySet();
    }

    //debug
    public static User getUserByString(String session) {
        return Optional.ofNullable(activeSession.get(session)).orElse(nullWrapper).getUser();
    }

    //debug
    //TODO доделать
    public static String calcTime(User user) {
        return String.format("online %4.2f min", 0);
    }

    private static void checkSessionLiveTime(HttpSession session) {
        if (activeSession.get(getShortHash(session)) != null) {
            UserSessionWrapper user = activeSession.get(getShortHash(session));
            if (System.currentTimeMillis() - user.getSessionStart() > SESSION_LIVE_TIME)
                activeSession.remove(getShortHash(session));
        }
    }

    private static boolean isActiveSession(HttpSession session) {
        return activeSession.get(getShortHash(session)) != null;
    }

    /**
     * Используется для получения текущего пользоваетля, который использует УМНУЮ ЛЕНТУ прямо сейчас по ту сторону экрана.
     * По сути этот метод нужен для определения вошёл ли пользоваетель в систему, используется в тех контроллерах, которым важна
     * авторизация пользователя.
     * @param session
     * @return
     */
    public static User getUserBySession(HttpSession session) {
        checkSessionLiveTime(session);
        return Optional.ofNullable(activeSession.get(getShortHash(session))).orElse(nullWrapper).getUser();
    }

    private static void removeSession(HttpSession session) {
        if (activeSession.get(getShortHash(session)) != null)
            activeSession.remove(getShortHash(session));
    }

    private static UserSessionWrapper putUser(User user) {
        return new UserSessionWrapper(user);
    }

    /**
     * Создаёт или обновляет текущую сессию. Привязывает пользователя к сессии. Нужна для определения авторизированных пользователей.
     * @param session {@link HttpSession} пользователя
     * @param user закреплённый за этой сессией пользователь.
     */

    public static void createOrUpdateSession(HttpSession session, User user) {
        checkSessionLiveTime(session);
        if (isActiveSession(session))
            activeSession.get(getShortHash(session)).setSessionStart(System.currentTimeMillis());
        else
            activeSession.put(getShortHash(session), putUser(user));
    }

    /**
     * Заканчивает сессию если она сушествует.
     * @param session текущая сессия
     */

    public static void logout(HttpSession session) {
        if (isActiveSession(session))
            removeSession(session);
    }

}
