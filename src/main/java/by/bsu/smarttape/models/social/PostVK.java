package by.bsu.smarttape.models.social;

import java.util.List;
import java.util.stream.Collectors;

public class PostVK implements Post {

    private final String headerImageUrl;
    private final String screenName;
    private final String headerTittle;
    private final String description;
    private final List<Attachment> attachmentList;

    public PostVK(String screenName, String headerImageUrl, String headerTittle, String description, List<Attachment> attachmentList) {
        this.screenName = screenName;
        this.description = description;
        this.headerTittle = headerTittle;
        this.headerImageUrl = headerImageUrl;
        this.attachmentList = attachmentList;
    }

    @Override
    public String getShortName() {
        return screenName;
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
    public String getTime() {
        return "вчера";
    }

    @Override
    public List<Attachment> getAttachmentList() {
        return attachmentList;
    }

    @Override
    public List<Attachment> getAttachmentListPicture() {
        return attachmentList.stream()
                .filter(attachment -> attachment.getType() == Attachment.IMAGE)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attachment> getAttachmentListVideo() {
        return attachmentList.stream()
                .filter(attachment -> attachment.getType() == Attachment.VIDEO)
                .collect(Collectors.toList());
    }
}
