package net.roxymc.jserialize.token;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface NumberToken<T extends Number> extends ScalarToken<T> {
}
