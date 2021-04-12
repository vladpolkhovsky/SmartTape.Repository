package by.bsu.smarttape.controllers;

import by.bsu.smarttape.models.Link;
import by.bsu.smarttape.models.Package;
import by.bsu.smarttape.utils.presentation.HeaderModel;
import by.bsu.smarttape.utils.results.PackageStatus;
import by.bsu.smarttape.utils.services.BasicPackageService;
import by.bsu.smarttape.utils.services.LinkService;
import by.bsu.smarttape.utils.services.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    @GetMapping("/package")
    public String registerForm(Model model) {
        HeaderModel header = HeaderModel.getInstance(null, true);
        PackageService basicPackageService = BasicPackageService.getInstance();
        PackageStatus packageStatus = basicPackageService.getPackage(1);
        Package aPackage = packageStatus.getPackage();
        model.addAttribute("header", header);
        model.addAttribute("package", aPackage);
        return "views/settings/packageSettings";
    }

    @PostMapping("/package")
    public String registerSubmit(Model model,
            @ModelAttribute Link link,
            HttpServletRequest request
                                 //@RequestParam(value = "link") String link,
                                 //@RequestParam(value = "package-id") long packageID
                                 ){
        request.getParameterMap().forEach((x, y) -> {
            System.out.println(x + ": ");
            Arrays.stream(y).forEach(z -> System.out.print(z + " "));
            System.out.println();
        });
        model.addAttribute("link", link);
        PackageStatus packageStatus = BasicPackageService.getInstance().getPackage(1);
        Package package1 = packageStatus.getPackage();
        List<Link> links = package1.getLinks();
        links.add(link);
        package1.setLinks(links);
        return "views/settings/packageSettings";
    }

}
