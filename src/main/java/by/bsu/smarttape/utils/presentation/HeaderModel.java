package by.bsu.smarttape.utils.presentation;

import by.bsu.smarttape.models.User;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class HeaderModel implements Serializable {

    private final User user;
    private final boolean showNav;

    private HeaderModel(User user, boolean showNav) {
        this.user = user;
        this.showNav = showNav;
    }

    public static HeaderModel getInstance(User user, boolean showNavBar) {
        return new HeaderModel(user, showNavBar);
    }

    public User getUser() {
        return user;
    }

    public boolean isEntered() {
        return user != null;
    }

    public static class Button {

        private final String name;

        private final String link;

        private final boolean active;

        public Button(String name, String link, boolean active) {
            this.name = name;
            this.link = link;
            this.active = active;
        }

        public String getLink() {
            return link;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }
    }

    public boolean isShowNav() {
        return showNav;
    }

    public List<Button> getButtons() {
        if (user != null)
            return Arrays.asList(
                    new Button("ПОПУЛЯРНОЕ", "/nl-feed?package=trends", true),
                    new Button("МОЯ ЛЕНТА", "/nl-feed?package=general", false)
            );
        else
            return Arrays.asList(
                    new Button("ПОПУЛЯРНОЕ", "/nl-feed?package=trends", true)
            );
    }
}