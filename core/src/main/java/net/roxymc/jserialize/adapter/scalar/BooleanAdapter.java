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

public final class BooleanAdapter implements TypeAdapter<Boolean> {
    public static final TypeAdapter<Boolean> PRIMITIVE = new BooleanAdapter(boolean.class);
    public static final TypeAdapter<Boolean> BOXED = new BooleanAdapter(Boolean.class);

    private static final Factory FACTORY = Factory.composite(
            Factory.exact(PRIMITIVE),
            Factory.exact(BOXED)
    );

    private final TypeRef<Boolean> type;
    private final Class<Boolean> rawType;

    private BooleanAdapter(Class<Boolean> type) {
        this.type = TypeRef.of(type);
        this.rawType = type;
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable Boolean read(Reader reader, ReadContext ctx) throws IOException {
        if (reader.peek() == TokenTypes.NULL) {
            if (rawType.isPrimitive()) {
                throw new IllegalStateException("Cannot read null into primitive " + rawType.getSimpleName());
            }

            reader.readNull();
            return null;
        }

        if (reader.peek() == TokenTypes.STRING) {
            return Boolean.parseBoolean(reader.readString());
        }

        return reader.readBoolean();
    }

    @Override
    public void write(Writer writer, @Nullable Boolean value, WriteContext ctx) throws IOException {
        if (value == null) {
            if (rawType.isPrimitive()) {
                throw new IllegalStateException("Cannot write null for primitive " + rawType.getSimpleName());
            }

            writer.writeNull();
            return;
        }

        writer.writeBoolean(value);
    }

    @Override
    public TypeRef<? extends Boolean> type() {
        return type;
    }
}
