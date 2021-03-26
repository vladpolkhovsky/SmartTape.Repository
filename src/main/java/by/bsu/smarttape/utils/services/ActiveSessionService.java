package by.bsu.smarttape.utils.services;

import by.bsu.smarttape.models.User;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class ActiveSessionService {

    private static final long SESSION_LIVE_TIME = 900000;

    private static final Map<String, User> activeSession = new HashMap<>();

    public static void checkSessionLiveTime(HttpSession session) {
        if (activeSession.get(session.toString()) != null) {
            User user = activeSession.get(session.toString());
            if (System.currentTimeMillis() - user.getSessionStart() > SESSION_LIVE_TIME)
                activeSession.remove(session.toString());
        }
    }

    public static boolean isActiveSession(HttpSession session) {
        return activeSession.get(session.toString()) != null;
    }

    public static User getUserBySession(HttpSession session) {
        checkSessionLiveTime(session);
        User user = activeSession.get(session.toString());
        return user;
    }

    private static User updateUser(User user) {
        user.setSessionStart(System.currentTimeMillis());
        return user;
    }

    public static void createOrUpdateSession(HttpSession session, User user) {
        checkSessionLiveTime(session);
        if (isActiveSession(session))
            activeSession.get(session.toString()).setSessionStart(System.currentTimeMillis());
        else
            activeSession.put(session.toString(), updateUser(user));
    }

    public static void logout(HttpSession session) {
        if (isActiveSession(session))
            activeSession.remove(session.toString());
    }

}
