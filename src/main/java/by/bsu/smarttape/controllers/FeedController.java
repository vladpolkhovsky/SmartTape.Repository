package by.bsu.smarttape.controllers;

import by.bsu.smarttape.utils.AppContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FeedController {
    @GetMapping("/nl-feed")
    public String feed(Model model, HttpServletRequest request) {
        return "feed/nl-feed";
    }
}
