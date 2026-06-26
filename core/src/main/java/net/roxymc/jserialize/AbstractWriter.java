package net.roxymc.jserialize;

import net.roxymc.jserialize.token.TokenType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public abstract class AbstractWriter implements Writer {
    protected final UnsupportedOperationException notSupported(TokenType tokenType) {
        return new UnsupportedOperationException(tokenType + " is not supported");
    }
}
