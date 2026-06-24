package net.roxymc.jserialize.token;

import org.jspecify.annotations.Nullable;

public abstract class AbstractValueAccessor<V> implements ValueAccessor<V> {
    protected TokenType getRawTokenType(@Nullable Object raw) {
        if (raw == null) {
            return TokenTypes.NULL;
        } else if (raw instanceof String) {
            return TokenTypes.STRING;
        } else if (raw instanceof Boolean) {
            return TokenTypes.BOOLEAN;
        } else if (raw instanceof Float || raw instanceof Double) {
            return TokenTypes.DOUBLE;
        } else if (raw instanceof Long) {
            return TokenTypes.LONG;
        } else if (raw instanceof Number) {
            return TokenTypes.INT;
        } else if (raw instanceof byte[]) {
            return TokenTypes.BINARY;
        }

        throw new IllegalStateException();
    }

    protected Token toRawToken(@Nullable Object raw) {
        if (raw == null) {
            return Tokens.NULL;
        } else if (raw instanceof String) {
            return new StringToken((String) raw);
        } else if (raw instanceof Boolean) {
            return (boolean) raw ? BooleanToken.TRUE : BooleanToken.FALSE;
        } else if (raw instanceof Float || raw instanceof Double) {
            return new DoubleToken(((Number) raw).doubleValue());
        } else if (raw instanceof Long) {
            return new LongToken((long) raw);
        } else if (raw instanceof Number) {
            return new IntToken(((Number) raw).intValue());
        } else if (raw instanceof byte[]) {
            return new BinaryToken((byte[]) raw);
        }

        throw new IllegalStateException();
    }
}
