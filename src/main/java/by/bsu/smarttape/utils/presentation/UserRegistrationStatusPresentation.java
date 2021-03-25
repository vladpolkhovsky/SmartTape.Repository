package by.bsu.smarttape.utils.presentation;

import by.bsu.smarttape.utils.results.UserRegistrationStatus;
import org.springframework.ui.Model;

public class UserRegistrationStatusPresentation implements Presentation{

    UserRegistrationStatus status;

    private UserRegistrationStatusPresentation(UserRegistrationStatus status) {
        this.status = status;
    }

    public static UserRegistrationStatusPresentation of(UserRegistrationStatus status) {
        return new UserRegistrationStatusPresentation(status);
    }

    @Override
    public void updateModel(Model model) {
        model.addAttribute("status", status);
    }
}
