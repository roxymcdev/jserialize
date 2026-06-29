package net.roxymc.jserialize.adapter.scalar;

import io.leangen.geantyref.GenericTypeReflector;
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

final class BooleanAdapter implements TypeAdapter<Boolean> {
    @Override
    public @Nullable Boolean read(Reader reader, TypeRef<? extends Boolean> type, ReadContext ctx) throws IOException {
        checkAssignable(Boolean.class, GenericTypeReflector.box(type.getRawType()));

        if (reader.peek() == TokenTypes.NULL) {
            if (type.getRawType().isPrimitive()) {
                throw new IllegalStateException("Cannot read null into primitive " + type.getRawType());
            }

            reader.readNull();
            return null;
        }

        return reader.readBoolean();
    }

    @Override
    public void write(Writer writer, TypeRef<? extends Boolean> type, @Nullable Boolean value, WriteContext ctx) throws IOException {
        checkAssignable(Boolean.class, GenericTypeReflector.box(type.getRawType()));

        if (value == null) {
            if (type.getRawType().isPrimitive()) {
                throw new IllegalStateException("Cannot write null for primitive " + type.getRawType());
            }

            writer.writeNull();
            return;
        }

        writer.writeBoolean(value);
    }
}
