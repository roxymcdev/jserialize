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

final class CharacterAdapter implements TypeAdapter<Character> {
    @Override
    public @Nullable Character read(Reader reader, TypeRef<? extends Character> type, ReadContext ctx) throws IOException {
        checkAssignable(Character.class, GenericTypeReflector.box(type.getRawType()));

        if (reader.peek() == TokenTypes.NULL) {
            if (type.getRawType().isPrimitive()) {
                throw new IllegalStateException("Cannot read null into primitive " + type.getRawType());
            }

            reader.readNull();
            return null;
        }

        String value = reader.readString();
        if (value.length() != 1) {
            throw new IllegalStateException("Not a character: " + value);
        }

        return value.charAt(0);
    }

    @Override
    public void write(Writer writer, TypeRef<? extends Character> type, @Nullable Character value, WriteContext ctx) throws IOException {
        checkAssignable(Character.class, GenericTypeReflector.box(type.getRawType()));

        if (value == null) {
            if (type.getRawType().isPrimitive()) {
                throw new IllegalStateException("Cannot write null for primitive " + type.getRawType());
            }

            writer.writeNull();
            return;
        }

        writer.writeString(value.toString());
    }
}
