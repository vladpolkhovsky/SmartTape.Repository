package by.bsu.smarttape.controllers;

import by.bsu.smarttape.models.Link;
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

    int basePackageId = 1;

    PackageStatus basePackage = BasicPackageService.getInstance().getPackage(basePackageId);

    @GetMapping(value = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPost(
            @RequestParam("package-id") String id,
            @RequestParam("offset") int offset,
            @RequestParam("count") int count
        ) {

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
