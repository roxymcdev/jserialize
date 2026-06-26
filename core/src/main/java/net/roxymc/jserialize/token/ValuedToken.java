package net.roxymc.jserialize.token;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ValuedToken<T> extends Token {
    T value();

    @Override
    TokenType.Valued<T> type();
}
