package by.bsu.smarttape.controllers;

import by.bsu.smarttape.models.Link;
import by.bsu.smarttape.models.Package;
import by.bsu.smarttape.models.User;
import by.bsu.smarttape.utils.presentation.HeaderModel;
import by.bsu.smarttape.utils.results.PackageStatus;
import by.bsu.smarttape.utils.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    @GetMapping("/")
    public String settingWork(Model model,
                              HttpServletRequest request){
        User user = ActiveSessionService.getUserBySession(request.getSession());
        HeaderModel header = HeaderModel.getInstance(user, false);
        model.addAttribute("header", header);
        return "views/settings/package";
    }

    private static class LinkWrapper {
        public boolean hidden = true;
        public String url;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LinkWrapper)) return false;
            LinkWrapper that = (LinkWrapper) o;
            return hidden == that.hidden && Objects.equals(url, that.url);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hidden, url);
        }
    }

    private Package updatePackage(int ownerId, Map<String, String[]> map) {
        Package aPackage = new Package();
        List<Link> links = new ArrayList<>();

        aPackage.setOwnerID(ownerId);

        Set<String> newLink = new HashSet<>();
        Map<Integer, LinkWrapper> integerStringMap = new HashMap<>();

        map.entrySet().forEach(entry -> {
            String[] keyWords = entry.getKey().split("\\+");
            if (keyWords[0].equals("name")) {
                aPackage.setName(entry.getValue()[0]);
                aPackage.setId(Integer.parseInt(keyWords[1]));
            } else {
                if (keyWords[0].equals("url") || keyWords[0].equals("checkbox")) {
                    int pid = Integer.parseInt(keyWords[1]);
                    int lid = Integer.parseInt(keyWords[2]);
                    if (lid == 0 && keyWords[0].equals("url"))
                        newLink.add(entry.getValue()[0]);
                    if (lid != 0 && keyWords[0].equals("url")) {
                        integerStringMap.putIfAbsent(Integer.parseInt(keyWords[2]), new LinkWrapper());
                        integerStringMap.get(Integer.parseInt(keyWords[2])).url = entry.getValue()[0];
                    }
                    if (lid != 0 && keyWords[0].equals("checkbox")) {
                        integerStringMap.putIfAbsent(Integer.parseInt(keyWords[2]), new LinkWrapper());
                        integerStringMap.get(Integer.parseInt(keyWords[2])).hidden = entry.getValue()[0].equals("off");
                    }
                }
            }
        });
        for (String s : newLink) {
            links.add(new Link(s, aPackage.getId()));
        }
        for (Map.Entry<Integer, LinkWrapper> wrapperEntry : integerStringMap.entrySet()) {
            Link link = new Link(wrapperEntry.getValue().url, aPackage.getId());
            link.setHidden(wrapperEntry.getValue().hidden);
            link.setId(wrapperEntry.getKey());
            links.add(link);
        }
        aPackage.setLinks(links);
        return aPackage;
    }

    @PostMapping("/package")
    public RedirectView updater(Model model, HttpServletRequest request) {
        User user = ActiveSessionService.getUserBySession(request.getSession());
        request.getParameterMap().forEach((x, y) -> {
            System.out.println(x + ":" + Arrays.toString(y));
        });
        Package aPackage = updatePackage(Integer.parseInt(user.getId().toString()), request.getParameterMap());
        BasicPackageService.getInstance().updatePackage(aPackage);
        return new RedirectView("/settings/");
    }

/*
    @PostMapping("/package")
    public RedirectView post(Model model,
                             HttpServletRequest request) {
        //User user = ActiveSessionService.getUserBySession(request.getSession());
//        User user = new User("Nasia", "kuk@c.d", "password");
//        model.addAttribute("user", user);
        //Package[] packages = UserService.getUserPackages(user);

        PackageService basicPackageService = BasicPackageService.getInstance();
        PackageStatus packageStatus1 = basicPackageService.getPackage(1);
        PackageStatus packageStatus2 = basicPackageService.getPackage(2);
        Package[] packages = {packageStatus1.getPackage(), packageStatus2.getPackage()};
        model.addAttribute("packages", packages);

        List<Link> updLinks = new ArrayList<>();

        request.getParameterMap().forEach((x, y) -> {
            System.out.println(x + ": ");
            StringTokenizer tokenizer =  new StringTokenizer(x, "+");
            long id = Long.parseLong(tokenizer.nextToken());
            long ownerId = Long.parseLong(tokenizer.nextToken());
            Arrays.stream(y).forEach(z -> System.out.print(z + " "));
            Link link = new Link(y[0], ownerId);
            link.setId(id);

            updLinks.add(link);
            System.out.println();
        });

        packageStatus1.getPackage().setLinks(updLinks);

        BasicPackageService.getInstance().updatePackage(packageStatus1.getPackage());

        return new RedirectView("/settings/");
    }
*/
//    public void process(
//            final HttpServletRequest request, final HttpServletResponse response,
//            final ServletContext servletContext, final ITemplateEngine templateEngine)
//            throws Exception {
//
//        PackageService basicPackageService = BasicPackageService.getInstance();
//        PackageStatus packageStatus1 = basicPackageService.getPackage(1);
//        PackageStatus packageStatus2 = basicPackageService.getPackage(2);
//        Package[] packages1 = {packageStatus1.getPackage(), packageStatus2.getPackage()};
//        List<Package> packages = Arrays.asList(packages1.clone());
//        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
//        ctx.setVariable("packages", packages);
//
//        templateEngine.process("views/settings/package", ctx, response.getWriter());
//
//    }

//    @GetMapping("/package")
//    public String registerForm(Model model) {
//        HeaderModel header = HeaderModel.getInstance(null, true);
//        PackageService basicPackageService = BasicPackageService.getInstance();
//        PackageStatus packageStatus = basicPackageService.getPackage(1);
//        Package aPackage = packageStatus.getPackage();
//        model.addAttribute("header", header);
//        model.addAttribute("package", aPackage);
//        return "views/settings/package";
//    }
//
//    @PostMapping("/package")
//    public String registerSubmit(Model model,
//            @ModelAttribute Link link,
//            HttpServletRequest request
//                                 //@RequestParam(value = "link") String link,
//                                 //@RequestParam(value = "package-id") long packageID
//                                 ){
//        request.getParameterMap().forEach((x, y) -> {
//            System.out.println(x + ": ");
//            Arrays.stream(y).forEach(z -> System.out.print(z + " "));
//            System.out.println();
//        });
//        model.addAttribute("link", link);
//        PackageStatus packageStatus = BasicPackageService.getInstance().getPackage(1);
//        Package package1 = packageStatus.getPackage();
//        List<Link> links = package1.getLinks();
//        links.add(link);
//        package1.setLinks(links);
//        HeaderModel header = HeaderModel.getInstance(null, true);
//        PackageService basicPackageService = BasicPackageService.getInstance();
//        PackageStatus packageStatus1 = basicPackageService.getPackage(1);
//        Package aPackage = packageStatus1.getPackage();
//        model.addAttribute("header", header);
//        model.addAttribute("package", aPackage);
//        return "views/settings/package";
//    }

}
