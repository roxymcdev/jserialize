package net.roxymc.jserialize.adapter.bson;

import net.roxymc.jserialize.adapter.WriterAdapter;
import org.bson.codecs.EncoderContext;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;

final class BsonWriterAdapter implements WriterAdapter {
    private final ObjectCodec<?> codec;
    private final org.bson.BsonWriter writer;
    private final EncoderContext encoderCtx;

    BsonWriterAdapter(ObjectCodec<?> codec, org.bson.BsonWriter writer, EncoderContext encoderCtx) {
        this.codec = codec;
        this.writer = writer;
        this.encoderCtx = encoderCtx;
    }

    @Override
    public void writeStart() {
        writer.writeStartDocument();
    }

    @Override
    public void writeProperty(String name, Type type, @Nullable Object value) {
        writer.writeName(name);

        if (value != null) {
            encoderCtx.encodeWithChildContext(codec.getCodec(type), writer, value);
        } else {
            writer.writeNull();
        }
    }

    @Override
    public void writeEnd() {
        writer.writeEndDocument();
    }
}
