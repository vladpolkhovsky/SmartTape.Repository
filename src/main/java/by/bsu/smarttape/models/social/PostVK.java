package by.bsu.smarttape.models.social;

import java.util.List;

public class PostVK implements Post{

    private final String headerImageUrl;
    private final String headerTittle;
    private final String description;
    private final List<Attachment> attachmentList;

    public PostVK(String headerImageUrl, String headerTittle, String description, List<Attachment> attachmentList) {
        this.description = description;
        this.headerTittle = headerTittle;
        this.headerImageUrl = headerImageUrl;
        this.attachmentList = attachmentList;
    }

    @Override
    public String getHeaderTittle() {
        return headerTittle;
    }

    @Override
    public String getHeaderImageUrl() {
        return headerImageUrl;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }
}
