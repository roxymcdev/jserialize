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

import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

final class StringAdapter implements TypeAdapter<String> {
    @Override
    public @Nullable String read(Reader reader, TypeRef<? extends String> type, ReadContext ctx) throws IOException {
        checkAssignable(String.class, type.getRawType());

        if (reader.peek() == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        return reader.readString();
    }

    @Override
    public void write(Writer writer, TypeRef<? extends String> type, @Nullable String value, WriteContext ctx) throws IOException {
        checkAssignable(String.class, type.getRawType());

        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeString(value);
    }
}
