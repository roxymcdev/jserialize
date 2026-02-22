package net.roxymc.jserialize.adapter.bson;

import net.roxymc.jserialize.adapter.FormatUtils;
import net.roxymc.jserialize.adapter.MapLike;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonValue;
import org.bson.codecs.Codec;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import static net.roxymc.jserialize.adapter.bson.ObjectCodec.DEFAULT_DECODER_CONTEXT;
import static net.roxymc.jserialize.adapter.bson.ObjectCodec.ID_PROPERTY_NAME;

final class BsonUtils implements FormatUtils {
    private final ObjectCodec<?> codec;

    public BsonUtils(ObjectCodec<?> codec) {
        this.codec = codec;
    }

    @Override
    public String idPropertyName() {
        return ID_PROPERTY_NAME;
    }

    @Override
    public Class<?> rawType() {
        return ObjectCodec.RAW_TYPE;
    }

    @Override
    public MapLike createMap(Type mapType) {
        Codec<Map<?, ?>> mapCodec = codec.getCodec(mapType);

        return new MapLike() {
            private final BsonDocument document = new BsonDocument();

            @Override
            public void put(String key, Object value) {
                document.put(key, (BsonValue) value);
            }

            @Override
            public void putAll(Map<?, ?> map) {
                document.putAll(new BsonDocumentWrapper<>(map, mapCodec));
            }

            @Override
            public Map<?, ?> asMap() {
                return mapCodec.decode(document.asBsonReader(), DEFAULT_DECODER_CONTEXT);
            }

            @Override
            public Map<String, ?> asRawMap() {
                return Collections.unmodifiableMap(document);
            }
        };
    }
}
