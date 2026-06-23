package net.roxymc.jserialize.format.configurate;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.reader.TokenReader;
import net.roxymc.jserialize.token.writer.TokenWriter;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.IOException;

final class WrappedTypeSerializer<T> implements TypeAdapter<T> {
    private final TypeSerializer<T> serializer;

    WrappedTypeSerializer(TypeSerializer<T> serializer) {
        this.serializer = serializer;
    }

    @Override
    public T read(Reader reader, TypeRef<? extends T> type, ReadContext context) throws IOException {
        @SuppressWarnings("unchecked")
        TokenReader<ConfigurationNode> tokenReader = (TokenReader<ConfigurationNode>) reader;
        return serializer.deserialize(type.getAnnotatedType(), tokenReader.tokenizer().nextValue());
    }

    @Override
    public void write(Writer writer, TypeRef<? extends T> type, @Nullable T value, WriteContext context) throws IOException {
        @SuppressWarnings("unchecked")
        TokenWriter<ConfigurationNode> tokenWriter = (TokenWriter<ConfigurationNode>) writer;
        serializer.serialize(type.getAnnotatedType(), value, tokenWriter.detokenizer().value());
    }
}
