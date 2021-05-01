package by.bsu.smarttape.controllers;

import by.bsu.smarttape.models.Link;
import by.bsu.smarttape.models.Package;
import by.bsu.smarttape.models.User;
import by.bsu.smarttape.models.social.Attachment;
import by.bsu.smarttape.models.social.Post;
import by.bsu.smarttape.utils.results.PackageStatus;
import by.bsu.smarttape.utils.results.SimpleStatus;
import by.bsu.smarttape.utils.services.ActiveSessionService;
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
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/api")
public class ApiController {

    @GetMapping(value = "/create-new-package")
    public RedirectView createNewPackage(HttpServletRequest request) throws IOException {
        User user = ActiveSessionService.getUserBySession(request.getSession());
        if (user != null) {
            PackageStatus status = BasicPackageService.getInstance().savePackage(new Package(
                    new ArrayList<>(),
                    user.getId(),
                    "Новый пакет."
            ));
            if (status.getCode() == SimpleStatus.OK) {
                System.out.println("OK");
            }
        }
        return new RedirectView("/settings/");
    }

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
        User cUser = ActiveSessionService.getUserBySession(request.getSession());
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
        assert packages != null;
        for (Package aPackage : packages) {
            aPackage.setLinks(BasicPackageService.getInstance().getPackage(aPackage.getId()).getPackage().getLinks());
        }
        System.out.println(Arrays.toString(packages));
        JsonArray jsonArray = new JsonArray();
        try {
            packages = Arrays.stream(packages).sorted((a, b) -> Long.compare(a.getId(), b.getId())).toArray(Package[]::new);
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

    @GetMapping(value = "/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPost(
            @RequestParam("package-id") long id,
            @RequestParam("offset") int offset,
            @RequestParam("count") int count,
            HttpServletRequest request
        ) {

        User user = ActiveSessionService.getUserBySession(request.getSession());

        if (id != -1) {

            PackageStatus basePackage = BasicPackageService.getInstance().getPackage(id);

            List<Post> postList = new ArrayList<>();

            if (user == null || basePackage.getPackage().getOwnerID() != user.getId()) {
                basePackage = BasicPackageService.getInstance().getPackage(BasicPackageService.NON_LOGON_PACKAGE);
            }

            for (Link link : basePackage.getPackage().getLinks()) {
                try {
                    if (!link.isHidden())
                        postList.addAll(VKParser.getInstance(link.getUrlAddress()).getPosts(offset * count, count, basePackage.getPackage()));
                } catch (ParserException e) {
                    System.err.println(e.getMessage());
                }
            }

            postList.sort(Comparator.comparingLong(Post::getTime).reversed());

            JsonObject jsonObject = new JsonObject();
            JsonArray postArray = new JsonArray();

            for (Post post : postList) {
                parsePostToJson(post, String.valueOf(id), postArray);
            }

            jsonObject.add("response", postArray);

            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);

        } else {

            PackageStatus basePackage;

            List <Post> postList = new ArrayList<>();

            basePackage = BasicPackageService.getInstance().getPackage(BasicPackageService.NON_LOGON_PACKAGE);

            if (user != null) {
                Package[] packages = UserService.getUserPackages(user);
                if (packages == null)
                    packages = new Package[] { basePackage.getPackage() };
                for (Package aPackage : packages) {
                    List<Link> links = BasicPackageService.getInstance().getPackage(aPackage.getId()).getPackage().getLinks();
                    for (Link link : links) {
                        try {
                            if (!link.isHidden())
                                postList.addAll(VKParser.getInstance(link.getUrlAddress()).getPosts(offset * count, count, aPackage));
                        } catch (ParserException e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
            }

            JsonObject jsonObject = new JsonObject();
            JsonArray postArray = new JsonArray();

            for (Post post
                    : postList.stream()
                    .sorted((o1, o2) -> -Long.compare(o1.getTime(), o2.getTime()))
                    .collect(Collectors.toList())) {
                parsePostToJson(post, String.valueOf(id), postArray);
            }

            jsonObject.add("response", postArray);

            return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);

        }

    }

    private void parsePostToJson(Post post, String pId, JsonArray postArray) {
        JsonObject postJson = new JsonObject();
        postJson.addProperty("header_tittle", post.getHeaderTittle());
        postJson.addProperty("header_url", post.getHeaderImageUrl());
        postJson.addProperty("header_short_name", post.getShortName());
        postJson.addProperty("description", post.getDescription());
        postJson.addProperty("time", timeBeautifier(post.getTime()));
        Package aPackage = post.getPackage();
        postJson.addProperty("package_name", aPackage == null ? "Популярное в ленте" : "Пакет \"" + aPackage.getName() + "\""); //TODO Название пакетов!!!.
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
        java.util.Date dateTime = new java.util.Date((long)((24 * 60 * 60 + time) * 1000));
        Locale locale = new Locale("ru");
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm",locale);
        return df.format(dateTime);
    }

}
