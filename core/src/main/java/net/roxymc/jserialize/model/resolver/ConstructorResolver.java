package net.roxymc.jserialize.model.resolver;

import net.roxymc.jserialize.model.constructor.ConstructorModel;
import org.jspecify.annotations.Nullable;

public interface ConstructorResolver {
    @Nullable ConstructorModel resolveConstructor(Class<?> clazz) throws IllegalAccessException;
}
