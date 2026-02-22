package net.roxymc.jserialize.model.resolver;

import net.roxymc.jserialize.model.constructor.ConstructorModel;
import net.roxymc.jserialize.model.property.PropertyMap;
import org.jspecify.annotations.Nullable;

public interface PropertiesResolver {
    PropertyMap resolveProperties(Class<?> clazz, @Nullable ConstructorModel constructor) throws IllegalAccessException;
}
