package by.bsu.smarttape.utils.services;

import by.bsu.smarttape.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.*;


public class ActiveSessionService {

    private static final long SESSION_LIVE_TIME = 900000 / 15 / 3;

    private static final Map<String, UserSessionWrapper> activeSession = initService();

    private static Timer cleaner;

    private static volatile boolean isLocked = false;
    private static volatile boolean isWorking = false;

    private static final Logger logger = LoggerFactory.getLogger(ActiveSessionService.class.getName());

    private static Map<String, UserSessionWrapper> initService() {
        cleaner = new Timer();

        cleaner.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        while (isWorking);
                        //System.out.println("cleaner task");
                        isLocked = true;
                        if (activeSession != null) {
                            List<String> timeOutSession = new ArrayList<>();
                            long cTime = System.currentTimeMillis();
                            for (Map.Entry<String, UserSessionWrapper> wrapperEntry : activeSession.entrySet())
                                if (cTime - wrapperEntry.getValue().getSessionStart() > SESSION_LIVE_TIME) {
                                    //System.out.println(wrapperEntry.getValue().getUser().getUserName() + " session time out.");
                                    timeOutSession.add(wrapperEntry.getKey());
                                }
                            timeOutSession.forEach(activeSession::remove);
                        }
                        isLocked = false;
                    }
                },
                SESSION_LIVE_TIME / 4,
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
        while(isLocked || isWorking);
        isWorking = true;
        Set<String> keySet = activeSession.keySet();
        isWorking = false;
        return keySet;
    }

    //debug
    public static User getUserByString(String session) {
        while(isLocked || isWorking);
        isWorking = true;
        User user = Optional.ofNullable(activeSession.get(session)).orElse(nullWrapper).getUser();
        isWorking = false;
        return user;
    }

    //debug
    public static String calcTime(String session) {
        double time = 0;
        while(isLocked || isWorking);
        isLocked = true;
        for (Map.Entry<String, UserSessionWrapper> entry : activeSession.entrySet())
            if (entry.getKey().equals(session))
                time = (System.currentTimeMillis() - entry.getValue().getSessionStart()) / 1000.0 / 60;
        isLocked = false;
        return String.format("online %4.2f min", time);
    }

    private static void checkSessionLiveTime(HttpSession session) {
        while (isLocked);
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
     * @param session {@link HttpSession}
     * @return User приаязанный к этой сессии или null, если такого нет.
     */

    public static User getUserBySession(HttpSession session) {
        while(isLocked || isWorking);
        isWorking = true;
        checkSessionLiveTime(session);
        User user = Optional.ofNullable(activeSession.get(getShortHash(session))).orElse(nullWrapper).getUser();
        if (user != null)
            createOrUpdateSession(session, user, false);
        isWorking = false;
        return user;
    }

    private static void removeSession(HttpSession session) {
        while (isLocked);
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

    private static void createOrUpdateSession(HttpSession session, User user, boolean checkWork) {
        while (isLocked || (checkWork && isWorking));
        isWorking = true;
        checkSessionLiveTime(session);
        if (isActiveSession(session))
            activeSession.get(getShortHash(session)).setSessionStart(System.currentTimeMillis());
        else
            activeSession.put(getShortHash(session), putUser(user));
        isWorking = false;
    }

    public static void createOrUpdateSession(HttpSession session, User user) {
       createOrUpdateSession(session, user, true);
    }

    /**
     * Заканчивает сессию если она сушествует.
     * @param session текущая сессия
     */

    public static void logout(HttpSession session) {
        while(isLocked || isWorking);
        isWorking = true;
        if (isActiveSession(session))
            removeSession(session);
        isWorking = false;
    }

}
