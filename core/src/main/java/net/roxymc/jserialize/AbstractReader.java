package net.roxymc.jserialize;

import net.roxymc.jserialize.token.TokenType;

import java.util.function.Predicate;

import static java.lang.String.format;

public abstract class AbstractReader implements Reader {
    protected final void checkToken(TokenType current, TokenType expected) {
        if (current != expected) {
            throw new IllegalStateException(format(
                    "Expected %s, but found %s", expected, current
            ));
        }
    }

    protected final void checkToken(TokenType tokenType, Predicate<TokenType> predicate) {
        if (!predicate.test(tokenType)) {
            throw new IllegalStateException(tokenType + " does not support this operation");
        }
    }

    protected final UnsupportedOperationException notSupported(TokenType tokenType) {
        return new UnsupportedOperationException(tokenType + " is not supported");
    }
}
