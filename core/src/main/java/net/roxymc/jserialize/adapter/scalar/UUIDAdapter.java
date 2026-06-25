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

import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

final class UUIDAdapter implements TypeAdapter<UUID> {
    @Override
    public @Nullable UUID read(Reader reader, TypeRef<? extends UUID> type, ReadContext context) throws IOException {
        checkAssignable(UUID.class, type.getRawType());

        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        return UUID.fromString(reader.readString());
    }

    @Override
    public void write(Writer writer, TypeRef<? extends UUID> type, @Nullable UUID value, WriteContext context) throws IOException {
        checkAssignable(UUID.class, type.getRawType());

        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeString(value.toString());
    }
}
