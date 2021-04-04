package by.bsu.smarttape.controllers;

import by.bsu.smarttape.models.Package;
import by.bsu.smarttape.models.User;
import by.bsu.smarttape.models.social.Post;
import by.bsu.smarttape.utils.services.ActiveSessionService;
import by.bsu.smarttape.utils.services.BasicPackageService;
import by.bsu.smarttape.utils.services.DataBaseSessionService;
import by.bsu.smarttape.utils.services.social.SocialParser;
import by.bsu.smarttape.utils.services.social.VKParser;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/debug")
public class DebugController {

    public static class SessionInfo {
        private User user;
        private String session;

        public SessionInfo(User user, String session) {
            this.user = user;
            this.session = session;
        }

        public String getSession() {
            return session;
        }

        public User getUser() {
            return user;
        }

        public void setSession(String session) {
            this.session = session;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    @GetMapping("/sessions")
    public String getSessions(Model model) {
        List<SessionInfo> infoList = new ArrayList<>();
        for (String session : ActiveSessionService.getSessions())
            infoList.add(new SessionInfo(ActiveSessionService.getUserByString(session), session));
        for (SessionInfo info : infoList)
            System.out.println(info.getSession() + " | " + info.getUser().getId());
        model.addAttribute("items", infoList);
        model.addAttribute("nowtime", System.currentTimeMillis());
        return "views/debug/debug-sessions";
    }

    // ПРОСТО ПРИМЕР. Вывод всех данных.
    public static class PackageWrapper {
        String error;
        List<Package> packages;
        public PackageWrapper() {
            Exception exception = DataBaseSessionService.safetyOperation(session -> {
                CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
                CriteriaQuery<Package> criteriaQuery = criteriaBuilder.createQuery(Package.class);
                Root<Package> userRoot = criteriaQuery.from(Package.class);
                criteriaQuery.select(userRoot);
                TypedQuery<Package> query = session.createQuery(criteriaQuery);
                packages = query.getResultList();
            });
            if(exception != null)
                error = exception.getMessage();
            else {
                for (int i = 0; i < packages.size(); i++) {
                    packages.set(
                        i,
                        //ПОЛУЧЕНИЕ СТАНДАРТНЫМ СПОСОБОМ.
                        BasicPackageService
                            .getInstance()
                            .getPackage(
                                packages.get(i).getId()
                            ).getPackage()
                    );
                }
            }
        }

        public String getError() {
            return error;
        }

        public List<Package> getPackages() {
            return packages;
        }
    }

    @GetMapping("/sandbox")
    public String sandbox(HttpServletRequest request, Model model) {
        model.addAttribute("packages", new PackageWrapper());
        return "views/debug/sandbox";
    }

    @GetMapping(value = "/post-checker", produces = MediaType.TEXT_HTML_VALUE + ";charset=utf8")
    public ResponseEntity<String> postChecker(@RequestParam("url") String url) {
        SocialParser parser = VKParser.parserBuilder(url);

        StringBuilder builder = new StringBuilder();
        builder.append("<body>");
        builder.append("<h1>").append("Посты").append("</h1>");
        builder.append("<hr />");

        List<Post> posts = parser.getPosts(20);

        for (Post post : posts)
            addToBuilder(builder, post);

        builder.append("</body>");

        return new ResponseEntity<>(builder.toString(), HttpStatus.OK);
    }

    private void addToBuilder(StringBuilder builder, Post post) {
        builder.append("<div style=\"width: 80%; margin: 0 auto;\">");
        builder.append("<img src=\"").append(post.getHeaderImageUrl()).append("\" />");
        builder.append("<h2>").append(post.getHeaderTittle()).append("</h2>");
        builder.append("<h3>").append(toCorrectHtmlString(post.getDescription())).append("</h3>");
        builder.append("<hr/>");
        builder.append("</div>");
    }

    private String toCorrectHtmlString(String description) {
        StringBuilder builder = new StringBuilder();
        System.out.println(description);
        String[] lines = description.split("\n");
        for (String line : lines)
            builder.append("<p>").append(line).append("</p>");
        return builder.toString();
    }

}
