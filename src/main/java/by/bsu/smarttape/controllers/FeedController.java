package by.bsu.smarttape.controllers;

import by.bsu.smarttape.models.User;
import by.bsu.smarttape.models.social.Post;
import by.bsu.smarttape.utils.presentation.HeaderModel;
import by.bsu.smarttape.utils.services.ActiveSessionService;
import by.bsu.smarttape.utils.services.social.VKParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Controller
public class FeedController {

    @GetMapping("/nl-feed")
    public String feed(Model model, @RequestParam(value = "url", required = false, defaultValue = "null") String url, HttpServletRequest request) {
        User user = ActiveSessionService.getUserBySession(request.getSession());
        HeaderModel header = HeaderModel.getInstance(user, true);
        model.addAttribute("header", header);
        if (url.equals("null")) {
            model.addAttribute("posts", new ArrayList<Post>());
            model.addAttribute("exception", null);
        } else {
            try {
                model.addAttribute("posts", VKParser.getInstance(url).getPosts(0, 100));
                model.addAttribute("exception", null);
            } catch (Exception ex) {
                model.addAttribute("posts", new ArrayList<Post>());
                model.addAttribute("exception", ex.getMessage());
                ex.printStackTrace();
            }
        }
        return "views/feed/nl-feed";
    }

}
