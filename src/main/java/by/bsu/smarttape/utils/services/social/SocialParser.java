package by.bsu.smarttape.utils.services.social;

import by.bsu.smarttape.models.social.Post;

import java.util.List;

public interface SocialParser {
    List<Post> getPosts(int limit);
}
