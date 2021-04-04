package by.bsu.smarttape.models.social;

public class Attachment {

    private final int type;

    public static final int IMAGE = 0;
    public static final int VIDEO = 1;

    private String url;

    public Attachment(int type, String url) {
        this.type = type;
        this.url = url;
    }

    public String getAttachmentUrl() {
        return url;
    }

    public int getType() {
        return type;
    }

}
