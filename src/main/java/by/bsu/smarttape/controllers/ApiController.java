package by.bsu.smarttape.controllers;

import by.bsu.smarttape.models.Link;
import by.bsu.smarttape.models.Package;
import by.bsu.smarttape.models.User;
import by.bsu.smarttape.models.social.Attachment;
import by.bsu.smarttape.models.social.Post;
import by.bsu.smarttape.utils.results.PackageStatus;
import by.bsu.smarttape.utils.services.BasicPackageService;
import by.bsu.smarttape.utils.services.UserService;
import by.bsu.smarttape.utils.services.social.VKParser;
import by.bsu.smarttape.utils.services.social.exceptions.ParserException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @GetMapping(value = "/packages-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkUserNameForUniq(HttpServletRequest request) throws IOException {
        User cUser = UserService.getUserByUserNameAndPassword("vlad.polkhovsky", "vlad.polkhovsky");
        JsonObject jsonObject = new JsonObject();
        if (cUser != null) {
            jsonObject.add("response", createResponse(cUser));
        } else {
            jsonObject.addProperty("error", "Время сессии истекло.");
        }
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    private JsonArray createResponse(User cUser) {
        Package[] packages = UserService.getUserPackages(cUser);
        for (Package aPackage : packages) {
            aPackage.setLinks(BasicPackageService.getInstance().getPackage(aPackage.getId()).getPackage().getLinks());
        }
        System.out.println(Arrays.toString(packages));
        JsonArray jsonArray = new JsonArray();
        try {
            for (Package aPackage : packages)
                jsonArray.add(createElement(aPackage));
        } catch (NullPointerException ignored) {
            System.err.println(ignored.getMessage());
        }
        return jsonArray;
    }

    private JsonObject createElement(Package aPackage) {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonObject.addProperty("name", aPackage.getName());
        jsonObject.addProperty("id", aPackage.getId());
        System.out.println(aPackage.getName());
        aPackage.getLinks().forEach(x -> {
            JsonObject object = new JsonObject();
            object.addProperty("lid", x.getId());
            object.addProperty("pid", x.getPackageId());
            object.addProperty("url", x.getUrlAddress());
            object.addProperty("hidden", x.isHidden());
            System.out.println(object);
            jsonArray.add(object);
        });
        jsonObject.add("links", jsonArray);
        return jsonObject;
    }

    int basePackageId = 1;

    @GetMapping(value = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPost(
            @RequestParam("package-id") long id,
            @RequestParam("offset") int offset,
            @RequestParam("count") int count
        ) {

        PackageStatus basePackage = BasicPackageService.getInstance().getPackage(id);

        List<Post> postList = new ArrayList<>();

        for (Link link : basePackage.getPackage().getLinks()) {
            try {
                postList.addAll(VKParser.getInstance(link.getUrlAddress()).getPosts(offset * count, count));
            } catch (ParserException e) {
                System.err.println(e.getMessage());
            }
        }

        postList.sort(Comparator.comparingLong(Post::getTime).reversed());

        JsonObject jsonObject = new JsonObject();
        JsonArray postArray = new JsonArray();

        for (Post post : postList) {
            parsePostToJson(post, postArray);
        }

        jsonObject.add("response", postArray);

        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    private void parsePostToJson(Post post, JsonArray postArray) {
        JsonObject postJson = new JsonObject();
        postJson.addProperty("header_tittle", post.getHeaderTittle());
        postJson.addProperty("header_url", post.getHeaderImageUrl());
        postJson.addProperty("header_short_name", post.getShortName());
        postJson.addProperty("description", post.getDescription());
        postJson.addProperty("time", timeBeautifier(post.getTime()));
        postJson.addProperty("package_name", "Больше в пакете Популярное.");
        postJson.add("attachments", parseAttachments(post.getAttachmentList()));
        postArray.add(postJson);
    }

    private JsonArray parseAttachments(List<Attachment> attachmentList) {
        JsonArray attArray = new JsonArray();
        for(Attachment attachment : attachmentList) {
            JsonObject object = new JsonObject();
            object.add("type", new JsonPrimitive(attachment.getType() == Attachment.IMAGE ? "Image" : "Video"));
            object.addProperty("url", attachment.getAttachmentUrl());
            attArray.add(object);
        }
        return attArray;
    }

    private String timeBeautifier(long time) {
        java.util.Date dateTime = new java.util.Date((long)time*1000);
        Locale locale = new Locale("ru");
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm",locale);
        return df.format(dateTime);
    }

}
