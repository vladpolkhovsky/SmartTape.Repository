package by.bsu.smarttape.utils.presentation;

import java.io.Serializable;

public class HeaderModel implements Serializable {
    private final String link;
    public final String caption;

    public HeaderModel(String link, String caption) {
        this.caption = caption;
        this.link = link;
    }

    public String getCaption() {
        return caption;
    }

    public String getLink() {
        return link;
    }

}