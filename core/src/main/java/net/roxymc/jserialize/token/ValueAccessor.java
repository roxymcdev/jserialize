package net.roxymc.jserialize.token;

import org.jspecify.annotations.Nullable;

public interface ValueAccessor<V> {
    String getName(V value);

    boolean isObject(V value);

    boolean isArray(V value);

    Iterable<? extends V> getValues(V value);

    V objectAppend(V container, String name);

    V listAppend(V container);

    void setScalar(V value, @Nullable Object raw);

    TokenType getTokenType(V value);

    Token toToken(V value);
}
