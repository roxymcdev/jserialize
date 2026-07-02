package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeRef;
import org.bson.AbstractBsonReader;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;

final class WrappedTypeAdapter<T> implements Codec<T> {
    private final TypeRef<? extends T> type;
    final TypeAdapter<T> typeAdapter;

    private final ReadContext readCtx;
    private final WriteContext writeCtx;

    WrappedTypeAdapter(TypeRef<? extends T> type, TypeAdapter<T> typeAdapter, TypeAdapters typeAdapters) {
        this.type = type;
        this.typeAdapter = typeAdapter;

        this.readCtx = ReadContext.of(typeAdapters, BsonUtils.INSTANCE);
        this.writeCtx = WriteContext.of(typeAdapters, BsonUtils.INSTANCE);
    }

    @Override
    public @Nullable T decode(BsonReader bsonReader, DecoderContext ctx) {
        Reader reader;

        if (bsonReader instanceof AbstractBsonReader) {
            reader = new StandardBsonReaderAdapter((AbstractBsonReader) bsonReader);
        } else {
            reader = new FallbackBsonReaderAdapter(bsonReader);
        }

        try {
            return typeAdapter.read(reader, readCtx);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void encode(BsonWriter bsonWriter, T value, EncoderContext ctx) {
        Writer writer = new BsonWriterAdapter(bsonWriter);

        try {
            typeAdapter.write(writer, value, writeCtx);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Class<T> getEncoderClass() {
        @SuppressWarnings("unchecked")
        Class<T> rawType = (Class<T>) type.getRawType();
        return rawType;
    }
}
