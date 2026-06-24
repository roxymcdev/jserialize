package net.roxymc.jserialize.token;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;
import java.util.function.Supplier;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

@ApiStatus.NonExtendable
public abstract class TokenType {
    private final Kind kind;

    private TokenType(Kind kind) {
        this.kind = kind;
    }

    @ApiStatus.Internal
    public static NonValued nonValued(Kind kind, Supplier<Token> token) {
        return new NonValued(kind, token);
    }

    @ApiStatus.Internal
    public static <T> Valued<T> valued(Kind kind, Function<T, ? extends ValuedToken<T>> tokenFactory) {
        return new Valued<>(kind, tokenFactory);
    }

    public static Virtual virtual(Kind kind) {
        return new Virtual(kind);
    }

    public Kind kind() {
        return kind;
    }

    public static final class NonValued extends TokenType {
        private final Supplier<Token> token;

        @ApiStatus.Internal
        private NonValued(Kind kind, Supplier<Token> token) {
            super(kind);
            this.token = token;
        }

        public Token create() {
            return token.get();
        }
    }

    public static final class Valued<T> extends TokenType {
        private final Function<T, ? extends ValuedToken<T>> tokenFactory;

        private Valued(Kind kind, Function<T, ? extends ValuedToken<T>> tokenFactory) {
            super(kind);
            this.tokenFactory = tokenFactory;
        }

        public ValuedToken<T> create(T value) {
            return tokenFactory.apply(nonNull(value, "value"));
        }
    }

    public static final class Virtual extends TokenType {
        private Virtual(Kind kind) {
            super(kind);
        }
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
