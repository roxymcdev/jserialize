package net.roxymc.jserialize.adapter.bson;

import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.ReaderAdapter;
import net.roxymc.jserialize.creator.PropertyValue;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.util.Pair;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.RawBsonDocument;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Iterator;

import static net.roxymc.jserialize.adapter.bson.ObjectCodec.DEFAULT_DECODER_CONTEXT;
import static net.roxymc.jserialize.adapter.bson.ObjectCodec.ID_PROPERTY_NAME;

final class BsonReaderAdapter implements ReaderAdapter {
    private final ObjectCodec<?> codec;
    private final BsonReader reader;
    private final DecoderContext decoderCtx;

    BsonReaderAdapter(ObjectCodec<?> codec, BsonReader reader, DecoderContext decoderCtx) {
        this.codec = codec;
        this.reader = reader;
        this.decoderCtx = decoderCtx;
    }

    @Override
    public Iterable<Pair<String, @Nullable PropertyModel>> properties() {
        return () -> new Iterator<>() {
            @Override
            public boolean hasNext() {
                return reader.readBsonType() != BsonType.END_OF_DOCUMENT;
            }

            @Override
            public Pair<String, @Nullable PropertyModel> next() {
                String name = reader.readName();
                PropertyModel property;

                if (ID_PROPERTY_NAME.equals(name)) {
                    property = codec.classModel.properties().idProperty();
                } else {
                    property = codec.classModel.properties().get(name, true);
                }

                return new Pair<>(name, property);
            }
        };
    }

    @Override
    public void readStart() {
        reader.readStartDocument();
    }

    @Override
    public PropertyValue<?> readValue(String name, Type type) {
        if (reader.getCurrentBsonType() == BsonType.NULL) {
            reader.readNull();
            return PropertyValue.NULL;
        }

        Codec<?> codec = this.codec.getCodec(type);

        if (codec instanceof ObjectCodec<?>) {
            return readPropertyValue((ObjectCodec<?>) codec);
        }

        return PropertyValue.of(decoderCtx.decodeWithChildContext(codec, reader));
    }

    private <U> PropertyValue<U> readPropertyValue(ObjectCodec<U> codec) {
        RawBsonDocument document = decoderCtx.decodeWithChildContext(
                this.codec.codecRegistry.get(RawBsonDocument.class), reader
        );

        return (parent, instance) -> codec.engine.read(
                new BsonReaderAdapter(codec, document.asBsonReader(), DEFAULT_DECODER_CONTEXT),
                new ReadContext<>(parent, instance)
        );
    }

    @Override
    public Object readRawValue(String name) {
        return decoderCtx.decodeWithChildContext(
                codec.codecRegistry.get(ObjectCodec.RAW_TYPE), reader
        );
    }

    @Override
    public void skipValue() {
        reader.skipValue();
    }

    @Override
    public void readEnd() {
        reader.readEndDocument();
    }
}
