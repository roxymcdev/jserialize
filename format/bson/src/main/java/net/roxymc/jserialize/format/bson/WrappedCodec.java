package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.type.TypeRef;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

final class WrappedCodec<T> implements TypeAdapter<T> {
    private final Codec<T> codec;

    WrappedCodec(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    public @Nullable T read(Reader reader, TypeRef<? extends T> type, ReadContext context) throws IOException {
        if (reader.peek() == TokenType.NULL) {
            return null;
        }

        try {
            return codec.decode(((BsonReaderAdapter) reader).getBsonReader(), DecoderContext.builder().build());
        } finally {
            if (reader instanceof FallbackBsonReaderAdapter) {
                ((FallbackBsonReaderAdapter) reader).resetToken();
            }
        }
    }

    @Override
    public void write(Writer writer, TypeRef<? extends T> type, @Nullable T value, WriteContext context) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        codec.encode(((BsonWriterAdapter) writer).writer, value, EncoderContext.builder().build());
    }
}
