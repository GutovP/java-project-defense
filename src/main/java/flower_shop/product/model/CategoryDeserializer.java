package flower_shop.product.model;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CategoryDeserializer implements JsonDeserializer<Category> {

    @Override
    public Category deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            // If it's a simple string like "flowers"
            String name = json.getAsString();
            return Category.builder().name(name).build();
        } else if (json.isJsonObject()) {
            // If it's a full object like { "name": "flowers" }
            JsonObject jsonObject = json.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.has("description") ? jsonObject.get("description").getAsString() : null;
            return Category.builder()
                    .name(name)
                    .description(description)
                    .build();
        } else {
            throw new JsonParseException("Invalid Category JSON");
        }
    }

}
