package net.roxymc.jserialize.model.property;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface GetterRef extends MethodRef {
    @Nullable Object get(Object instance) throws Throwable;
}
