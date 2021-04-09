package by.bsu.smarttape.utils.services.social;

import by.bsu.smarttape.models.social.Attachment;
import by.bsu.smarttape.models.social.Post;
import by.bsu.smarttape.models.social.PostVK;
import by.bsu.smarttape.utils.services.social.exceptions.BadSocialNetworkException;
import by.bsu.smarttape.utils.services.social.exceptions.ParserException;
import com.google.gson.*;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.enums.WallFilter;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.utils.DomainResolvedType;
import com.vk.api.sdk.objects.utils.responses.ResolveScreenNameResponse;
import com.vk.api.sdk.queries.groups.GroupsGetByIdQueryWithLegacy;
import com.vk.api.sdk.queries.users.UsersGetQuery;
import com.vk.api.sdk.queries.wall.WallGetQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class VKParser implements SocialParser {

    private static final Integer APP_ID = 7812818;
    private static final String SERVICE_KEY = "8861687088616870886168704988165ea28886188616870e8098ac3eafea732534a6dc2";
    private static final ServiceActor SERVICE_ACTOR = new ServiceActor(APP_ID, SERVICE_KEY);

    private final Integer id;
    private final String screenName;
    private final ServiceActor serviceActor;
    private final UserActor userActor;

    private final List<Post> list = new ArrayList<>();

    private VkApiClient vkApiClient = null;

    private void checkUrl(String url) throws BadSocialNetworkException {
        if (!Pattern.matches("(https://|)?(vk.com/)([\\w.])+", url))
            throw new BadSocialNetworkException(url, "VK");
    }

    private Integer resolveName(String screen_name) throws ParserException {
        try {
            ResolveScreenNameResponse screenNameResponse = vkApiClient.utils().resolveScreenName(serviceActor, screen_name).execute();
            return Integer.valueOf(
                    screenNameResponse.getType().getValue().equals(DomainResolvedType.USER.getValue()) ?
                            screenNameResponse.getObjectId().toString() : "-" + screenNameResponse.getObjectId().toString()
            );
        } catch (Exception ex) {
            throw new ParserException(
                    "Произошла ошибка. Скорее всего имя пользователя введено неверно.<br>"+
                    "<span class=\"error\">" + ex.getMessage() + "</span>"
            );
        }
    }

    private VKParser(String url, UserActor userActor) throws ParserException {
        this.userActor = userActor;
        this.serviceActor = SERVICE_ACTOR;
        checkUrl(url);
        initVkApi();
        try {
            this.screenName = url.substring(url.lastIndexOf('/') + 1);
            this.id = resolveName(url.substring(url.lastIndexOf('/') + 1));
        } catch (Exception ex) {
            throw new ParserException(ex.getMessage());
        }
        System.out.println(id);
    }

    public static VKParser getInstance(String url) throws ParserException {
        return getInstance(url, null);
    }

    public static VKParser getInstance(String url, UserActor userActor) throws ParserException {
        return new VKParser(url, userActor);
    }

    private void initVkApi() {
        TransportClient transportClient = HttpTransportClient.getInstance();
        this.vkApiClient = new VkApiClient(transportClient);
    }

    private WallGetQuery getWallGetQuery() {
        if (userActor != null)
            return vkApiClient.wall().get(userActor);
        return vkApiClient.wall().get(serviceActor);
    }

    private void parse(int start, int limit) throws ParserException {
        try {
            String wallQuery = getWallGetQuery()
                    .ownerId(id)
                    .offset(start)
                    .count(limit)
                    .filter(WallFilter.OWNER)
                    .executeAsString();
            list.addAll(parsePosts(wallQuery));
        } catch (Exception exception) {
            throw new ParserException(exception.getMessage());
        }
    }

    private List<Post> parsePosts(String wallQuery) {
        JsonObject response = JsonParser.parseString(wallQuery)
                .getAsJsonObject()
                .get("response")
                .getAsJsonObject();
        String headerUrl = getHeaderUrl();
        JsonArray items = response.get("items").isJsonNull() ?
                new JsonArray() : response.get("items").getAsJsonArray();
        List<Post> posts = new ArrayList<>();
        items.forEach((item) -> posts.add(parsePost(item, headerUrl)));
        return posts;
    }

    private String getUserHeaderUrl(UsersGetQuery query) {
        try {
            JsonElement element = JsonParser.parseString(query.userIds(id.toString()).fields(Fields.PHOTO_100).executeAsString());
            return element.getAsJsonObject().get("response")
                    .getAsJsonArray().get(0)
                    .getAsJsonObject()
                    .get("photo_100")
                    .getAsString();
        } catch (ClientException ex) {
            return null;
        }
    }

    private String getGroupHeaderUrl(GroupsGetByIdQueryWithLegacy query) {
        try {
            JsonElement element = JsonParser.parseString(query.groupIds(id.toString().substring(1)).executeAsString());
            return element.getAsJsonObject().get("response")
                    .getAsJsonArray().get(0)
                    .getAsJsonObject()
                    .get("photo_100")
                    .getAsString();
        } catch (ClientException ex) {
            return null;
        }
    }

    private String getHeaderUrl() {
        if (userActor == null) {
            if (id.toString().startsWith("-"))
                return getGroupHeaderUrl(vkApiClient.groups().getByIdLegacy(serviceActor));
            else
                return getUserHeaderUrl(vkApiClient.users().get(serviceActor));
        } else {
            if (id.toString().startsWith("-"))
                return getGroupHeaderUrl(vkApiClient.groups().getByIdLegacy(userActor));
            else
                return getUserHeaderUrl(vkApiClient.users().get(userActor));
        }
    }

    private Post parsePost(JsonElement item, String profileUrl) {
        String text = item.getAsJsonObject().get("text").getAsString();
        List<Attachment> attachments = new ArrayList<>();
        try {
            if (item.getAsJsonObject().get("copy_history") != null && !item.getAsJsonObject().get("copy_history").isJsonNull()) {
                JsonArray copy_array = item.getAsJsonObject().get("copy_history").getAsJsonArray();
                item = copy_array.get(copy_array.size() - 1);
                return parsePost(getRepostOwner(item), getHeaderUrl());
            } else {
                item.getAsJsonObject().get("attachments").getAsJsonArray().forEach((x) -> {
                    Attachment attachment = parseAttachment(x);
                    if (attachment != null)
                        attachments.add(attachment);
                });
            }
        } catch (Exception ignored) {

        }
        return new PostVK(
                screenName,
                profileUrl,
                "VK",
                text,
                attachments
        );
    }

    private JsonElement getRepostOwner(JsonElement item) throws ClientException {
        return JsonParser.parseString(
                vkApiClient.wall()
                        .getByIdLegacy(
                                serviceActor,
                                item.getAsJsonObject().get("owner_id").getAsString()+"_"+item.getAsJsonObject().get("id").getAsString()
                        )
                        .executeAsString()
        ).getAsJsonObject().get("response").getAsJsonArray().get(0);
    }

    private Attachment parseAttachment(JsonElement attachment) {
        int type = attachment.getAsJsonObject().get("type").getAsString().equals("photo") ?
                Attachment.IMAGE : Attachment.VIDEO;
        if (type == Attachment.IMAGE) {
            JsonArray photos = attachment
                    .getAsJsonObject()
                    .get("photo")
                    .getAsJsonObject()
                    .get("sizes")
                    .getAsJsonArray();
            return new Attachment(
                    type,
                    photos.get(photos.size() - 1)
                            .getAsJsonObject()
                            .get("url").getAsString()
            );
        }
        return null;
    }

    @Override
    public List<Post> getPosts(int start, int limit) {
        list.clear();
        try {
            parse(start, limit);
        } catch (ParserException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return list;
    }
}
