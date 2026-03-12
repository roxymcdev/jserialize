package net.roxymc.jserialize.token;

import org.jspecify.annotations.Nullable;

public abstract class AbstractValueAccessor<V> implements ValueAccessor<V> {
    protected TokenType getRawTokenType(@Nullable Object raw) {
        if (raw == null) {
            return TokenType.NULL;
        } else if (raw instanceof String) {
            return TokenType.STRING;
        } else if (raw instanceof Boolean) {
            return TokenType.BOOLEAN;
        } else if (raw instanceof Float || raw instanceof Double) {
            return TokenType.DOUBLE;
        } else if (raw instanceof Long) {
            return TokenType.LONG;
        } else if (raw instanceof Number) {
            return TokenType.INT;
        } else if (raw instanceof byte[]) {
            return TokenType.BINARY;
        } else {
            return TokenType.UNKNOWN;
        }
    }

    protected ScalarToken<?> toRawToken(@Nullable Object raw) {
        if (raw == null) {
            return ScalarToken.NULL;
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
        } else {
            return new UnknownToken(raw);
        }
    }
}
