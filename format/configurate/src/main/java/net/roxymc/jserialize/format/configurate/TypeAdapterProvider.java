package net.roxymc.jserialize.format.configurate;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.io.IOException;
import java.lang.reflect.AnnotatedType;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

// unfortunately configurate doesn't give us a better way to implement this
public final class TypeAdapterProvider implements TypeSerializer.Annotated<Object> {
    final TypeAdapters adapters;

    public TypeAdapterProvider(TypeAdapters adapters) {
        this.adapters = nonNull(adapters, "adapters");
    }

    public boolean hasTypeAdapter(AnnotatedType type) {
        return adapters.get(TypeToken.of(type)) != null;
    }

    @Override
    public @Nullable Object deserialize(AnnotatedType type, ConfigurationNode node) throws SerializationException {
        TypeToken<Object> typeToken = TypeToken.of(type);
        TypeAdapter<Object> typeAdapter = adapters.getOrThrow(typeToken);

        Reader reader = ConfigurateUtils.newReader0(node);

        try {
            ReadContext context = ReadContext.of(new ConfigurateTypeAdapters(node.options(), adapters), ConfigurateUtils.INSTANCE);

            return typeAdapter.read(reader, typeToken, context);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void serialize(AnnotatedType type, @Nullable Object obj, ConfigurationNode node) throws SerializationException {
        TypeToken<Object> typeToken = TypeToken.of(type);
        TypeAdapter<Object> typeAdapter = adapters.getOrThrow(typeToken);

        Writer writer = ConfigurateUtils.newWriter0(node);

        try {
            WriteContext context = WriteContext.of(new ConfigurateTypeAdapters(node.options(), adapters), ConfigurateUtils.INSTANCE);

            typeAdapter.write(writer, typeToken, obj, context);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
