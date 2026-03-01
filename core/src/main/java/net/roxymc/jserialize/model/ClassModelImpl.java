package net.roxymc.jserialize.model;

import net.roxymc.jserialize.model.constructor.ConstructorModel;
import net.roxymc.jserialize.model.property.PropertyMap;
import net.roxymc.jserialize.util.ObjectUtils;
import org.jspecify.annotations.Nullable;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class ClassModelImpl<T> implements ClassModel<T> {
    private final Class<T> clazz;
    private final @Nullable ConstructorModel constructor;
    private final PropertyMap properties;

    ClassModelImpl(Class<T> clazz, @Nullable ConstructorModel constructor, PropertyMap properties) {
        this.clazz = clazz;
        this.constructor = constructor;
        this.properties = nonNull(properties, "properties");
    }

    @Override
    public Class<T> clazz() {
        return clazz;
    }

    @Override
    public @Nullable ConstructorModel constructor() {
        return constructor;
    }

    @Override
    public PropertyMap properties() {
        return properties;
    }

    @Override
    public String toString() {
        return ObjectUtils.toString(this)
                .add("clazz=" + clazz)
                .add("constructor=" + constructor)
                .add("properties=" + properties.values())
                .toString();
    }
}
