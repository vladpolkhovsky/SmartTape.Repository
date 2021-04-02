package by.bsu.smarttape.utils.results;

import by.bsu.smarttape.models.Link;

public class LinkStatus implements SimpleStatus {

    private final int code;

    private final String message;
    private final Link link;

    public LinkStatus(int code, String message, Link link) {
        this.code = code;
        this.message = message;
        this.link = link;
    }

    public static LinkStatus createStatus(int code, String message, Link link) {
        return new LinkStatus(code, message, link);
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Link getLink() {
        return link;
    }

    public int getCode() {
        return code;
    }
}
