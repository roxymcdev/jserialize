package net.roxymc.jserialize.format.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.adapter.object.FormatUtils;
import net.roxymc.jserialize.adapter.object.MapLike;
import net.roxymc.jserialize.format.TokenTypeRegistry;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.util.Collections;
import java.util.Map;

final class GsonUtils implements FormatUtils<JsonElement> {
    static final TokenTypeRegistry<JsonToken, JsonReader, JsonWriter> TOKEN_TYPES = TokenTypeRegistry.create(builder -> builder
            .bind(TokenTypes.NAME, JsonToken.NAME, JsonReader::nextName, JsonWriter::name)
            .bind(TokenTypes.OBJECT_START, JsonToken.BEGIN_OBJECT, JsonReader::beginObject, JsonWriter::beginObject)
            .bind(TokenTypes.OBJECT_END, JsonToken.END_OBJECT, JsonReader::endObject, JsonWriter::endObject)
            .bind(TokenTypes.ARRAY_START, JsonToken.BEGIN_ARRAY, JsonReader::beginArray, JsonWriter::beginArray)
            .bind(TokenTypes.ARRAY_END, JsonToken.END_ARRAY, JsonReader::endArray, JsonWriter::endArray)
            .bind(TokenTypes.STRING, JsonToken.STRING, JsonReader::nextString, JsonWriter::value)
            .bind(TokenTypes.BOOLEAN, JsonToken.BOOLEAN, JsonReader::nextBoolean, JsonWriter::value)
            .bind(TokenTypes.INT, null, JsonReader::nextInt, JsonWriter::value)
            .bind(TokenTypes.LONG, null, JsonReader::nextLong, JsonWriter::value)
            .bind(TokenTypes.DOUBLE, null, JsonReader::nextDouble, JsonWriter::value)
            .bind(TokenTypes.NUMERIC, JsonToken.NUMBER)
            .bind(TokenTypes.NULL, JsonToken.NULL, JsonReader::nextNull, JsonWriter::nullValue)
    );

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
        TypeRef<Map<?, ?>> typeRef = TypeRef.of(mapType);
        TypeAdapter<Map<?, ?>> mapAdapter = typeAdapters.getOrThrow(typeRef);

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
                    mapAdapter.write(new GsonWriterAdapter(writer), typeRef, map, ctx);

                    result = writer.get().getAsJsonObject();
                }

                object.asMap().putAll(result.asMap());
            }

            @Override
            public @Nullable Map<?, ?> asMap(@Nullable Map<?, ?> instance, ReadContext ctx) throws IOException {
                try (JsonTreeReader reader = new JsonTreeReader(object)) {
                    Reader readerAdapter = new GsonReaderAdapter(reader);

                    if (!(mapAdapter instanceof TypeAdapter.Mutable)) {
                        return mapAdapter.read(readerAdapter, typeRef, ctx);
                    }

                    return ((TypeAdapter.Mutable<Map<?, ?>>) mapAdapter).mutate(readerAdapter, typeRef, instance, ctx);
                }
            }

            @Override
            public Map<String, JsonElement> asRawMap() {
                return Collections.unmodifiableMap(object.asMap());
            }
        };
    }
}
