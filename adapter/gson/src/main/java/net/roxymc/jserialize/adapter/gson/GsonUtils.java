package net.roxymc.jserialize.adapter.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.reflect.TypeToken;
import net.roxymc.jserialize.adapter.FormatUtils;
import net.roxymc.jserialize.adapter.MapLike;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

final class GsonUtils implements FormatUtils<JsonElement> {
    private final ObjectAdapter<?> adapter;

    public GsonUtils(ObjectAdapter<?> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Class<JsonElement> rawType() {
        return ObjectAdapter.RAW_TYPE;
    }

    @Override
    public MapLike<JsonElement> createMap(Type mapType) {
        @SuppressWarnings("unchecked")
        TypeAdapter<Map<?, ?>> mapAdapter = (TypeAdapter<Map<?, ?>>) adapter.gson.getAdapter(TypeToken.get(mapType));

        return new MapLike<>() {
            private final JsonObject object = new JsonObject();

            @Override
            public void put(String key, JsonElement value) {
                object.add(key, value);
            }

            @Override
            public void putAll(Map<?, ?> map) throws Throwable {
                JsonTreeWriter writer = new JsonTreeWriter();
                mapAdapter.write(writer, map);

                JsonObject mapObject = writer.get().getAsJsonObject();
                object.asMap().putAll(mapObject.asMap());
            }

            @Override
            public Map<?, ?> asMap() throws Throwable {
                return mapAdapter.read(new JsonTreeReader(object));
            }

            @Override
            public Map<String, JsonElement> asRawMap() {
                return Collections.unmodifiableMap(object.asMap());
            }
        };
    }
}
