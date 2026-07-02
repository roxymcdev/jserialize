package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

public final class StringAdapter implements TypeAdapter<String> {
    private static final TypeRef<String> TYPE = TypeRef.of(String.class);

    public static final TypeAdapter<String> INSTANCE = new StringAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private StringAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable String read(Reader reader, ReadContext ctx) throws IOException {
        TokenType tokenType = reader.peek();

        if (tokenType == TokenTypes.NULL) {
            reader.readNull();
            return null;
        }

        if (tokenType == TokenTypes.STRING || tokenType == TokenTypes.NUMERIC) {
            return reader.readString();
        }

        if (tokenType == TokenTypes.INT) {
            return String.valueOf(reader.readInt());
        }

        if (tokenType == TokenTypes.LONG) {
            return String.valueOf(reader.readLong());
        }

        if (tokenType == TokenTypes.DOUBLE) {
            return String.valueOf(reader.readDouble());
        }

        throw new IllegalStateException("Expected string token, but found: " + tokenType);
    }

    @Override
    public void write(Writer writer, @Nullable String value, WriteContext ctx) throws IOException {
        if (value == null) {
            writer.writeNull();
            return;
        }

        writer.writeString(value);
    }

    @Override
    public TypeRef<? extends String> type() {
        return TYPE;
    }
}
