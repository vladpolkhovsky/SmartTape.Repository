package by.bsu.smarttape.controllers;

import by.bsu.smarttape.models.User;
import by.bsu.smarttape.models.social.Post;
import by.bsu.smarttape.utils.presentation.HeaderModel;
import by.bsu.smarttape.utils.services.ActiveSessionService;
import by.bsu.smarttape.utils.services.social.VKParser;
import org.hibernate.mapping.Array;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Controller
public class FeedController {

    @GetMapping("/nl-feed")
    public String feed(Model model, @RequestParam(value = "url", required = false, defaultValue = "null") String url, HttpServletRequest request) {
        User user = ActiveSessionService.getUserBySession(request.getSession());
        HeaderModel header = HeaderModel.getInstance(user, true);
        model.addAttribute("header", header);
        if (url.equals("null"))
            model.addAttribute("posts", new ArrayList<Post>());
        else {
            try {
                model.addAttribute("posts", VKParser.parserBuilder(url).getPosts(10));
            } catch (Exception ex) {
                model.addAttribute("posts", new ArrayList<Post>());
            }
        }
        return "views/feed/nl-feed";
    }
}
