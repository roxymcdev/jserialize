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

public final class CharacterAdapter implements TypeAdapter<Character> {
    public static final TypeAdapter<Character> PRIMITIVE = new CharacterAdapter(char.class);
    public static final TypeAdapter<Character> BOXED = new CharacterAdapter(Character.class);

    private static final Factory FACTORY = Factory.composite(
            Factory.exact(PRIMITIVE),
            Factory.exact(BOXED)
    );

    private final TypeRef<Character> type;
    private final Class<Character> rawType;

    private CharacterAdapter(Class<Character> type) {
        this.type = TypeRef.of(type);
        this.rawType = type;
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable Character read(Reader reader, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            if (rawType.isPrimitive()) {
                throw new IllegalStateException("Cannot read null into primitive " + rawType.getSimpleName());
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
    public void write(Writer writer, @Nullable Character value, WriteContext ctx) throws IOException {
        if (value == null) {
            if (rawType.isPrimitive()) {
                throw new IllegalStateException("Cannot write null for primitive " + rawType.getSimpleName());
            }

            writer.writeNull();
            return;
        }

        writer.writeString(value.toString());
    }

    @Override
    public TypeRef<? extends Character> type() {
        return type;
    }
}
