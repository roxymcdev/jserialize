package net.roxymc.jserialize.model.property;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface SetterRef extends MethodRef {
    void set(Object instance, @Nullable Object value) throws Throwable;
}
