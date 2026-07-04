package net.roxymc.jserialize.format.configurate;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.format.tree.TreeReader;
import net.roxymc.jserialize.format.tree.TreeWriter;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.IOException;

final class WrappedTypeSerializer<T> implements TypeAdapter<T> {
    private final TypeRef<T> type;
    private final TypeSerializer<T> serializer;

    WrappedTypeSerializer(TypeRef<T> type, TypeSerializer<T> serializer) {
        this.type = type;
        this.serializer = serializer;
    }

    @Override
    public T read(Reader reader, ReadContext context) throws IOException {
        @SuppressWarnings("unchecked")
        TreeReader<ConfigurationNode> tokenReader = (TreeReader<ConfigurationNode>) reader;
        return serializer.deserialize(type.getAnnotatedType(), tokenReader.currentValue());
    }

    @Override
    public void write(Writer writer, @Nullable T value, WriteContext context) throws IOException {
        @SuppressWarnings("unchecked")
        TreeWriter<ConfigurationNode> tokenWriter = (TreeWriter<ConfigurationNode>) writer;
        serializer.serialize(type.getAnnotatedType(), value, tokenWriter.currentValue());
    }

    @Override
    public TypeRef<? extends T> type() {
        return type;
    }
}
