package by.bsu.smarttape;

import by.bsu.smarttape.utils.AppContext;
import by.bsu.smarttape.utils.SimpleRequestResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

@Controller
public class FeedController {
    @GetMapping("/nl-feed")
    public String feed(Model model, HttpServletRequest request) {
        String keysValue = ((SimpleRequestResolver)AppContext.getBean("requestResolver")).resolve(request);
        System.out.println(keysValue);
        model.addAttribute("keysFragment", keysValue);
        return "feed/nl-feed";
    }
}
