package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public final class UUIDAdapter implements TypeAdapter<UUID> {
    private static final TypeRef<UUID> TYPE = TypeRef.of(UUID.class);

    public static final TypeAdapter<UUID> INSTANCE = new UUIDAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private UUIDAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable UUID read(Reader reader, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        return UUID.fromString(reader.readString());
    }

    @Override
    public void write(Writer writer, @Nullable UUID value, WriteContext ctx) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeString(value.toString());
    }

    @Override
    public TypeRef<? extends UUID> type() {
        return TYPE;
    }
}
