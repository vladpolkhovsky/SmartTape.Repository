package by.bsu.smarttape;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FeedController {
    @GetMapping("/nl-feed")
    public String feed() {
        return "feed/nl-feed";
    }
}
