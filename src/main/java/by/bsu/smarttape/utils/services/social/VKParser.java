package by.bsu.smarttape.utils.services.social;

import by.bsu.smarttape.models.social.Post;
import by.bsu.smarttape.models.social.PostVK;
import com.google.gson.*;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.responses.GetByIdLegacyResponse;
import com.vk.api.sdk.objects.utils.responses.ResolveScreenNameResponse;
import com.vk.api.sdk.queries.wall.WallGetQuery;

import java.util.ArrayList;
import java.util.List;

public class VKParser implements SocialParser {

    private static final Integer APP_ID = 7812818;
    private static final String SERVICE_KEY = "8861687088616870886168704988165ea28886188616870e8098ac3eafea732534a6dc2";
    private static final ServiceActor SERVICE_ACTOR = new ServiceActor(APP_ID, SERVICE_KEY);

    private String screen_name;

    private List<Post> list;

    private VKParser(String url) {
        this.screen_name = url.substring(url.lastIndexOf('/') + 1);
        System.out.println("screen_name " + screen_name);
        parse();
    }

    private void parse() {
        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient);
        try {
            JsonObject jsonObject = new JsonObject();

            ResolveScreenNameResponse screenNameResponse = vk.utils()
                    .resolveScreenName(SERVICE_ACTOR, screen_name)
                    .execute();

            List<GetByIdLegacyResponse> groupInfo = vk.groups().getByIdLegacy(SERVICE_ACTOR)
                    .groupIds(screenNameResponse.getObjectId().toString())
                    .execute();

            WallGetQuery wallGetQuery = vk.wall()
                    .get(SERVICE_ACTOR)
                    .ownerId(-screenNameResponse.getObjectId())
                    .count(90);
            String wallQuery = wallGetQuery.executeAsString();

            JsonElement response = JsonParser.parseString(wallQuery);

            if (response.getAsJsonObject().get("error") == null) {

                int count = response.getAsJsonObject()
                        .get("response").getAsJsonObject()
                        .get("count").getAsInt();

                JsonArray items = response.getAsJsonObject()
                        .get("response").getAsJsonObject()
                        .get("items").getAsJsonArray();

                list = new ArrayList<>();

                for (JsonElement item : items) {
                    list.add(parsePostElement(item, groupInfo.get(0).getName(), groupInfo.get(0).getPhoto200().toString()));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private PostVK parsePostElement(JsonElement item, String headerName, String headerUrl) {
        return new PostVK (
                headerUrl,
                headerName,
                item.getAsJsonObject().get("text").getAsString(),
                null
        );
    }

    public static VKParser parserBuilder(String url) {
        return new VKParser(url);
    }

    @Override
    public List<Post> getPosts(int limit) {
        return list.subList(0, limit);
    }
}
