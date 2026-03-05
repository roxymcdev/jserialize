package net.roxymc.jserialize.model.resolver;

import net.roxymc.jserialize.model.constructor.ConstructorModel;

public interface ConstructorResolver {
    void resolveConstructor(Class<?> clazz, ConstructorModel.Builder constructor);
}
