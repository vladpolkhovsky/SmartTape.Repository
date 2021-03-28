package by.bsu.smarttape.controllers;

import by.bsu.smarttape.utils.services.UserService;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping(value = "/api")
public class ApiController {

    @GetMapping(value = "/user-name-checker", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkUserNameForUniq(@RequestParam("user-name") String userName) throws IOException {
        JsonObject jsonObject = new JsonObject();
        boolean result = UserService.isUserNameRegistered(userName);
        jsonObject.addProperty("user_name", userName);
        jsonObject.addProperty("already_exists", result);
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

}
