package net.roxymc.jserialize.format.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.adapter.object.FormatUtils;
import net.roxymc.jserialize.adapter.object.MapLike;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.util.Collections;
import java.util.Map;

final class GsonUtils implements FormatUtils<JsonElement> {
    static final GsonUtils INSTANCE = new GsonUtils();

    private GsonUtils() {
    }

    @Override
    public Class<JsonElement> rawType() {
        return JsonElement.class;
    }

    @Override
    public Reader newReader(JsonElement raw) {
        return new GsonReaderAdapter(new JsonTreeReader(raw));
    }

    @Override
    public MapLike<JsonElement> createMap(TypeAdapters typeAdapters, AnnotatedType mapType) {
        TypeToken<Map<?, ?>> typeToken = TypeToken.of(mapType);
        TypeAdapter<Map<?, ?>> mapAdapter = typeAdapters.getOrThrow(typeToken);

        return new MapLike<>() {
            private final JsonObject object = new JsonObject();

            @Override
            public void put(String key, @Nullable JsonElement value) {
                object.add(key, value);
            }

            @Override
            public void putAll(Map<?, ?> map, WriteContext ctx) throws IOException {
                JsonObject result;

                try (JsonTreeWriter writer = new JsonTreeWriter()) {
                    mapAdapter.write(new GsonWriterAdapter(writer), typeToken, map, ctx);

                    result = writer.get().getAsJsonObject();
                }

                object.asMap().putAll(result.asMap());
            }

            @Override
            public @Nullable Map<?, ?> asMap(ReadContext ctx) throws IOException {
                try (JsonTreeReader reader = new JsonTreeReader(object)) {
                    return mapAdapter.read(new GsonReaderAdapter(reader), typeToken, ctx);
                }
            }

            @Override
            public Map<String, JsonElement> asRawMap() {
                return Collections.unmodifiableMap(object.asMap());
            }
        };
    }
}
