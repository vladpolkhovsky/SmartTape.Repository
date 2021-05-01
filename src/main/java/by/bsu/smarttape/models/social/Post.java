package by.bsu.smarttape.models.social;

import by.bsu.smarttape.models.Package;

import java.util.List;

public interface Post {
    String getShortName();
    String getHeaderTittle();
    String getHeaderImageUrl();
    String getDescription();
    long getTime();
    List<Attachment> getAttachmentList();
    List<Attachment> getAttachmentListPicture();
    List<Attachment> getAttachmentListVideo();
    Package getPackage();
}
