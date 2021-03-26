package by.bsu.smarttape.controllers;

import by.bsu.smarttape.forms.UserRegistrationForm;
import by.bsu.smarttape.models.User;
import by.bsu.smarttape.utils.UserService;
import by.bsu.smarttape.utils.presentation.UserRegistrationStatusPresentation;
import by.bsu.smarttape.utils.services.ActiveSessionService;
import by.bsu.smarttape.utils.services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public RedirectView login(
            HttpServletRequest request,
            @RequestParam(value = "user-name") String userName,
            @RequestParam(value = "password") String password
    ) {
        User user = UserService.getUserByUserNameAndPassword(userName, password);
        if (user != null) {
            ActiveSessionService.createOrUpdateSession(request.getSession(), user);
            System.out.println(user.getId() + " logged.");
        }
        return new RedirectView("/nl-feed");
    }

    @GetMapping("/registration-form")
    public String register() {
        return "views/registration/registrationForm";
    }


}
