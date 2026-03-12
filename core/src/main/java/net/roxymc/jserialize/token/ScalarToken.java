package net.roxymc.jserialize.token;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface ScalarToken<T extends @Nullable Object> extends Token {
    ScalarToken<@Nullable Object> NULL = NullToken.INSTANCE;

    T value();
}
