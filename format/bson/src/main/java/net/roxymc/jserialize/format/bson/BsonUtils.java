package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.adapter.object.FormatUtils;
import net.roxymc.jserialize.adapter.object.MapLike;
import net.roxymc.jserialize.type.TypeRef;
import org.bson.*;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.AnnotatedType;
import java.util.Collections;
import java.util.Map;

final class BsonUtils implements FormatUtils<BsonValue> {
    static final BsonUtils INSTANCE = new BsonUtils();

    private BsonUtils() {
    }

    @Override
    public String idPropertyName() {
        return "_id";
    }

    @Override
    public Class<BsonValue> rawType() {
        return BsonValue.class;
    }

    @Override
    public Reader newReader(BsonValue raw) {
        if (raw instanceof BsonDocument) {
            return new StandardBsonReaderAdapter(new BsonDocumentReader((BsonDocument) raw));
        }

        BsonDocument document = new BsonDocument("", raw);

        BsonDocumentReader reader = new BsonDocumentReader(document);
        reader.readStartDocument();
        reader.readName();

        return new StandardBsonReaderAdapter(reader);
    }

    @Override
    public MapLike<BsonValue> createMap(TypeAdapters typeAdapters, AnnotatedType mapType) {
        TypeRef<Map<?, ?>> typeRef = TypeRef.of(mapType);
        TypeAdapter<Map<?, ?>> mapAdapter = typeAdapters.getOrThrow(typeRef);

        return new MapLike<>() {
            private final BsonDocument document = new BsonDocument();

            @Override
            public void put(String key, @Nullable BsonValue value) {
                document.put(key, value != null ? value : BsonNull.VALUE);
            }

            @Override
            public void putAll(Map<?, ?> map, WriteContext ctx) throws IOException {
                BsonDocument result = new BsonDocument();

                try (BsonDocumentWriter writer = new BsonDocumentWriter(result)) {
                    mapAdapter.write(new BsonWriterAdapter(writer), typeRef, map, ctx);
                }

                document.putAll(result);
            }

            @Override
            public @Nullable Map<?, ?> asMap(@Nullable Map<?, ?> instance, ReadContext ctx) throws IOException {
                try (BsonDocumentReader reader = new BsonDocumentReader(document)) {
                    Reader readerAdapter = new StandardBsonReaderAdapter(reader);

                    if (!(mapAdapter instanceof TypeAdapter.Mutable)) {
                        return mapAdapter.read(readerAdapter, typeRef, ctx);
                    }

                    return ((TypeAdapter.Mutable<Map<?, ?>>) mapAdapter).mutate(readerAdapter, typeRef, instance, ctx);
                }
            }

            @Override
            public Map<String, BsonValue> asRawMap() {
                return Collections.unmodifiableMap(document);
            }
        };
    }
}
