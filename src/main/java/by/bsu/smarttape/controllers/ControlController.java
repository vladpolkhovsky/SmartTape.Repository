package by.bsu.smarttape.controllers;

import by.bsu.smarttape.forms.UserUpdateForm;
import by.bsu.smarttape.models.User;
import by.bsu.smarttape.utils.presentation.HeaderModel;
import by.bsu.smarttape.utils.results.UserUpdateStatus;
import by.bsu.smarttape.utils.services.ActiveSessionService;
import by.bsu.smarttape.utils.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/control-panel")
public class ControlController {

    @GetMapping("/profile-settings")
    public String getProfileSettings(HttpServletRequest request, Model model) {
        User user = ActiveSessionService.getUserBySession(request.getSession());
        HeaderModel header = HeaderModel.getInstance(user, false);
        model.addAttribute("header", header);
        model.addAttribute("status", null);
        return "views/control-panel/profile";
    }

    @PostMapping("/profile-settings")
    public String updateProfile(HttpServletRequest request, Model model) {
        User user = ActiveSessionService.getUserBySession(request.getSession());

        if (user != null) {
            UserUpdateForm updateForm = new UserUpdateForm(request);

            UserUpdateStatus status = UserService.UpdateUser(request, updateForm);
            User newUser = ActiveSessionService.getUserBySession(request.getSession());

            model.addAttribute("header", HeaderModel.getInstance(newUser, false));
            model.addAttribute("status", status);
        } else {
            model.addAttribute("header", HeaderModel.getInstance(null, false));
        }

        return "views/control-panel/profile.html";
    }

}
