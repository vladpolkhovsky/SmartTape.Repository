package by.bsu.smarttape.utils.services;

import by.bsu.smarttape.forms.UserRegistrationForm;
import by.bsu.smarttape.utils.results.SimpleStatus;
import by.bsu.smarttape.utils.results.UserRegistrationStatus;

public class RegistrationService {

    public static UserRegistrationStatus register(UserRegistrationForm registrationForm) {
        return UserRegistrationStatus.createStatus(Math.random() > 0.5 ? SimpleStatus.OK : SimpleStatus.ERROR, "Test message");
    }

}
