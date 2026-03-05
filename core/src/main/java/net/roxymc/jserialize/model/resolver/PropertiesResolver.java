package net.roxymc.jserialize.model.resolver;

import net.roxymc.jserialize.model.constructor.ConstructorModel;
import net.roxymc.jserialize.model.property.PropertyMap;

public interface PropertiesResolver {
    void resolveProperties(Class<?> clazz, PropertyMap.Builder properties);

    void resolveProperties(ConstructorModel constructor, PropertyMap.Builder properties);
}
