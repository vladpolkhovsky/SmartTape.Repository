package by.bsu.smarttape.controllers;

import by.bsu.smarttape.models.User;
import by.bsu.smarttape.models.social.Post;
import by.bsu.smarttape.utils.presentation.HeaderModel;
import by.bsu.smarttape.utils.services.ActiveSessionService;
import by.bsu.smarttape.utils.services.BasicPackageService;
import by.bsu.smarttape.utils.services.UserService;
import by.bsu.smarttape.utils.services.social.VKParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Controller
public class FeedController {

    @GetMapping("/nl-feed")
    public String feed(Model model, HttpServletRequest request) {
        User user = ActiveSessionService.getUserBySession(request.getSession());

        HeaderModel header = HeaderModel.getInstance(user, true);
        model.addAttribute("header", header);

        if (user != null) {
            try {
                model.addAttribute("packages", Arrays.asList(Objects.requireNonNull(UserService.getUserPackages(user))));
            } catch (Exception ex) {
                model.addAttribute("exception", ex.getMessage());
            }
        }

        return "views/feed/nl-feed";
    }
}
