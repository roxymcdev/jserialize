package net.roxymc.jserialize.token;

import net.roxymc.jserialize.util.TypeUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;
import java.util.function.Supplier;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public abstract class TokenType {
    private final String name;
    private final Kind kind;

    private TokenType(String name, Kind kind) {
        this.name = name;
        this.kind = kind;
    }

    @ApiStatus.Internal
    public static NonValued nonValued(String name, Kind kind, Supplier<Token> token) {
        return new NonValued(name, kind, token);
    }

    @ApiStatus.Internal
    public static <T> Valued<T> valued(String name, Kind kind, Function<T, ? extends ValuedToken<T>> tokenFactory) {
        return new Valued<>(name, kind, tokenFactory);
    }

    @ApiStatus.Internal
    public static Virtual virtual(String name, Kind kind) {
        return new Virtual(name, kind);
    }

    public Kind kind() {
        return kind;
    }

    public static final class NonValued extends TokenType {
        private final Supplier<Token> token;

        @ApiStatus.Internal
        private NonValued(String name, Kind kind, Supplier<Token> token) {
            super(name, kind);
            this.token = token;
        }

        public Token create() {
            return token.get();
        }
    }

    public static final class Valued<T> extends TokenType {
        private final Function<T, ? extends ValuedToken<T>> tokenFactory;

        private Valued(String name, Kind kind, Function<T, ? extends ValuedToken<T>> tokenFactory) {
            super(name, kind);
            this.tokenFactory = tokenFactory;
        }

        public ValuedToken<T> create(T value) {
            return tokenFactory.apply(nonNull(value, "value"));
        }
    }

    public static final class Virtual extends TokenType {
        private Virtual(String name, Kind kind) {
            super(name, kind);
        }
    }

    @Override
    public String toString() {
        return TypeUtils.getShortCanonicalName(getClass()) + "[" + name + "]";
    }

    public enum Kind {
        NAME(false),
        STRUCTURE_START(true),
        STRUCTURE_END(false),
        SCALAR(true),
        NULL(true),
        END(false),
        ;

        private final boolean marksValue;

        Kind(boolean marksValue) {
            this.marksValue = marksValue;
        }

        public boolean marksValue() {
            return marksValue;
        }
    }
}
