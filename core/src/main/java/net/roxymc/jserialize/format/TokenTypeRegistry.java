package net.roxymc.jserialize.format;

import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.util.IOBiConsumer;
import net.roxymc.jserialize.util.IOConsumer;
import net.roxymc.jserialize.util.IOFunction;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public final class TokenTypeRegistry<N, R, W> {
    private final Map<TokenType, TokenTypeInfo<R, W>> registry;
    private final Map<N, TokenType> fromNative;

    private TokenTypeRegistry(Builder<N, R, W> builder) {
        this.registry = Collections.unmodifiableMap(new IdentityHashMap<>(builder.registry));
        this.fromNative = Map.copyOf(builder.fromNative);
    }

    public static <N, R, W> TokenTypeRegistry<N, R, W> create(UnaryOperator<Builder<N, R, W>> builder) {
        return new TokenTypeRegistry<>(builder.apply(new Builder<>()));
    }

    public TokenTypeInfo.@Nullable NonValued<R, W> get(TokenType.NonValued tokenType) {
        return (TokenTypeInfo.NonValued<R, W>) registry.get(tokenType);
    }

    public <T> TokenTypeInfo.@Nullable Valued<R, W, T> get(TokenType.Valued<T> tokenType) {
        @SuppressWarnings("unchecked")
        TokenTypeInfo.Valued<R, W, T> info = (TokenTypeInfo.Valued<R, W, T>) registry.get(tokenType);
        return info;
    }

    public TokenType fromNative(N nativeType) {
        TokenType tokenType = fromNative.get(nativeType);
        if (tokenType == null) {
            throw new IllegalStateException("Missing token type for " + nativeType);
        }

        return tokenType;
    }

    public static final class Builder<N, R, W> {
        private final Map<TokenType, TokenTypeInfo<R, W>> registry = new IdentityHashMap<>();
        private final Map<N, TokenType> fromNative = new HashMap<>();

        public Builder<N, R, W> bind(TokenType.NonValued tokenType, @Nullable N nativeType, IOConsumer<R> reader, IOConsumer<W> writer) {
            registry.put(tokenType, new TokenTypeInfo.NonValued<>(reader, writer));

            if (nativeType != null) {
                fromNative.put(nativeType, tokenType);
            }

            return this;
        }

        public <T> Builder<N, R, W> bind(TokenType.Valued<T> tokenType, @Nullable N nativeType, IOFunction<R, T> reader, IOBiConsumer<W, T> writer) {
            registry.put(tokenType, new TokenTypeInfo.Valued<>(reader, writer));

            if (nativeType != null) {
                fromNative.put(nativeType, tokenType);
            }

            return this;
        }

        public <T> Builder<N, R, W> bind(TokenType.Virtual tokenType, N nativeType) {
            fromNative.put(nativeType, tokenType);
            return this;
        }
    }
}
