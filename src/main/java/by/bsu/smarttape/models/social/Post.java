package by.bsu.smarttape.models.social;

import java.util.List;

public interface Post {
    String getShortName();
    String getHeaderTittle();
    String getHeaderImageUrl();
    String getDescription();
    String getTime();
    List<Attachment> getAttachmentList();
    List<Attachment> getAttachmentListPicture();
    List<Attachment> getAttachmentListVideo();
}
