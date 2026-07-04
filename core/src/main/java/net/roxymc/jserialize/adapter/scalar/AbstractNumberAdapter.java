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

abstract class AbstractNumberAdapter<N extends Number> implements TypeAdapter<N> {
    protected final TypeRef<N> type;
    protected final Class<N> rawType;

    protected AbstractNumberAdapter(Class<N> type) {
        this.type = TypeRef.of(type);
        this.rawType = type;
    }

    @Override
    public final @Nullable N read(Reader reader, ReadContext ctx) throws IOException {
        TokenType tokenType = reader.peek();

        if (tokenType == TokenTypes.NULL) {
            if (rawType.isPrimitive()) {
                throw new IllegalStateException("Cannot read null into primitive " + rawType.getSimpleName());
            }

            reader.readNull();
            return null;
        }

        if (tokenType == TokenTypes.INT) {
            return fromLong(reader.readInt());
        }

        if (tokenType == TokenTypes.LONG) {
            return fromLong(reader.readLong());
        }

        if (tokenType == TokenTypes.DOUBLE) {
            return fromDouble(reader.readDouble());
        }

        if (tokenType == TokenTypes.NUMERIC || tokenType == TokenTypes.STRING) {
            try {
                return parse(reader.readString());
            } catch (NumberFormatException e) {
                throw new ArithmeticException("Invalid format for " + rawType.getSimpleName() + ": " + e.getMessage());
            }
        }

        throw new IllegalStateException("Expected numeric token, but found: " + tokenType);
    }

    protected N fromDouble(double value) {
        if (!Double.isFinite(value)) {
            throw new ArithmeticException(rawType.getSimpleName() + " must be finite: " + value);
        }

        long longValue = (long) value;

        if (longValue != value) {
            throw new ArithmeticException(rawType.getSimpleName() + " overflow or loss of precision: " + value);
        }

        return fromLong(longValue);
    }

    protected abstract N fromLong(long value);

    protected abstract N parse(String value) throws NumberFormatException;

    @Override
    public final void write(Writer writer, @Nullable N value, WriteContext ctx) throws IOException {
        if (value == null) {
            if (rawType.isPrimitive()) {
                throw new IllegalStateException("Cannot write null for primitive " + rawType.getSimpleName());
            }

            writer.writeNull();
            return;
        }

        write(writer, value);
    }

    protected abstract void write(Writer writer, N value) throws IOException;

    @Override
    public TypeRef<? extends N> type() {
        return type;
    }
}
