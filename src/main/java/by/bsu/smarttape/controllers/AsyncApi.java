package by.bsu.smarttape.controllers;

import by.bsu.smarttape.utils.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping(value = "/api")
public class AsyncApi {

    @GetMapping(value = "/user-name-checker", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkUserNameForUniq(@RequestParam("user-name") String userName) throws IOException {
        JsonObject jsonObject = new JsonObject();
        UserService.UserServiceResult result = UserService.isUserNameRegistered(userName);
        jsonObject.addProperty("user_name", userName);
        jsonObject.addProperty("already_exists", result.isOk() ? (boolean)result.result() : null);
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

}
