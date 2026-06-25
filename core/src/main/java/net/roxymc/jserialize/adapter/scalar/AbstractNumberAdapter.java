package net.roxymc.jserialize.adapter.scalar;

import io.leangen.geantyref.GenericTypeReflector;
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

import static net.roxymc.jserialize.util.TypeChecks.checkAssignable;

abstract class AbstractNumberAdapter<N extends Number> implements TypeAdapter<N> {
    protected final Class<N> numberType;

    protected AbstractNumberAdapter(Class<N> numberType) {
        this.numberType = numberType;
    }

    @Override
    public final @Nullable N read(Reader reader, TypeRef<? extends N> type, ReadContext ctx) throws IOException {
        checkAssignable(numberType, GenericTypeReflector.box(type.getRawType()));

        TokenType tokenType = reader.peek();

        if (tokenType == TokenTypes.NULL) {
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
                throw new ArithmeticException("Invalid format for " + numberType.getSimpleName() + ": " + e.getMessage());
            }
        }

        throw new IllegalStateException("Expected numeric token, but found: " + tokenType);
    }

    protected N fromDouble(double value) {
        if (!Double.isFinite(value)) {
            throw new ArithmeticException(numberType.getSimpleName() + " must be finite: " + value);
        }

        long longValue = (long) value;

        if (longValue != value) {
            throw new ArithmeticException(numberType.getSimpleName() + " overflow or loss of precision: " + value);
        }

        return fromLong(longValue);
    }

    protected abstract N fromLong(long value);

    protected abstract N parse(String value) throws NumberFormatException;

    @Override
    public final void write(Writer writer, TypeRef<? extends N> type, @Nullable N value, WriteContext ctx) throws IOException {
        checkAssignable(numberType, GenericTypeReflector.box(type.getRawType()));

        if (value == null) {
            writer.writeNull();
            return;
        }

        write(writer, value);
    }

    protected abstract void write(Writer writer, N value) throws IOException;
}
