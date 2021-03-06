package by.bsu.smarttape.controllers;

import by.bsu.smarttape.forms.UserRegistrationForm;
import by.bsu.smarttape.models.User;
import by.bsu.smarttape.utils.presentation.HeaderModel;
import by.bsu.smarttape.utils.services.UserService;
import by.bsu.smarttape.utils.presentation.UserRegistrationStatusPresentation;
import by.bsu.smarttape.utils.services.ActiveSessionService;
import by.bsu.smarttape.utils.services.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class RegistrationController {

    @PostMapping("/registration-form")
    public String register(Model model, HttpServletRequest request) {
        UserRegistrationStatusPresentation.of(
                RegistrationService.register(
                        new UserRegistrationForm(request)
                )
        ).updateModel(model);
        return "views/registration/registrationStatus";
    }

    @PostMapping("/login")
    public RedirectView login(Model model,
            HttpServletRequest request,
            @RequestParam(value = "user-name") String userName,
            @RequestParam(value = "password") String password
    ) {
        ActiveSessionService.logout(request.getSession());
        User user = UserService.getUserByUserNameAndPassword(userName, password);
        if (user != null)
            ActiveSessionService.createOrUpdateSession(request.getSession(), user);
        return new RedirectView("/nl-feed");
    }

    @GetMapping("/registration-form")
    public String register(Model model) {
        HeaderModel header = HeaderModel.getInstance(null, false);
        model.addAttribute("header", header);
        return "views/registration/registrationForm";
    }

    @GetMapping("/logout")
    public RedirectView logout(HttpServletRequest request) {
        ActiveSessionService.logout(request.getSession());
        return new RedirectView("/nl-feed");
    }

}
