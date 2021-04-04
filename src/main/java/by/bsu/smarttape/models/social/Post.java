package by.bsu.smarttape.models.social;

import java.util.List;

public interface Post {
    String getHeaderTittle();
    String getHeaderImageUrl();
    String getDescription();
    List<Attachment> getAttachmentList();
}
