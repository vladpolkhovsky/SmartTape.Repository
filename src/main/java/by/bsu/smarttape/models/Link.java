package by.bsu.smarttape.models;

import by.bsu.smarttape.utils.results.LinkStatus;

enum SocialNetwork{vk, inst, tweet};

public class Link {
    private long id;
    private String urlAddress;

    private LinkStatus getLink(long packageID, SocialNetwork socialNetwork, long userID){
        return new LinkStatus(0, null,null);
    }

    private LinkStatus deleteLink(long linkID){
        return new LinkStatus(0, null,null);
    }

    private LinkStatus addLink(String urlAddress, long packageID, long userID){
        return new LinkStatus(0, null,null);
    }

}
