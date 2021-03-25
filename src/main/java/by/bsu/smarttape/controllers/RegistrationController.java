package by.bsu.smarttape.controllers;

import by.bsu.smarttape.forms.UserRegistrationForm;
import by.bsu.smarttape.utils.presentation.UserRegistrationStatusPresentation;
import by.bsu.smarttape.utils.services.RegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

        return "registration/registrationStatus";
    }

    @GetMapping("/registration-form")
    public String register() {
        return "registration/registrationForm";
    }

}
