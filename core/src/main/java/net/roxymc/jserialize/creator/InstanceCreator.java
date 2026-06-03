package net.roxymc.jserialize.creator;

import net.roxymc.jserialize.model.ClassModel;
import net.roxymc.jserialize.model.constructor.ConstructorModel;
import net.roxymc.jserialize.model.constructor.ParameterModel;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class InstanceCreator<T> {
    private final ClassModel<T> classModel;
    private final Map<PropertyModel, PropertyValue<?>> properties;

    private InstanceCreator(Builder<T> builder) {
        this.classModel = builder.classModel;
        this.properties = Map.copyOf(builder.properties);
    }

    public static <T> Builder<T> builder(ClassModel<T> classModel) {
        return new Builder<>(classModel);
    }

    public T createInstance() throws Throwable {
        ConstructorModel constructor = classModel.constructor();

        if (constructor == null) {
            throw new IllegalStateException("No constructor found for " + classModel.clazz());
        }

        ParameterModel[] parameters = constructor.parameters();
        @Nullable Object[] values = new Object[parameters.length];

        for (ParameterModel parameter : parameters) {
            String name = parameter.name();
            PropertyMeta meta = parameter.meta();

            PropertyValue<?> value = getValue(parameter);
            if (value == null) {
                validateValue(name, null, meta);
                continue;
            }

            // TODO parent should be passed here
            Object resolvedValue = value.getSafe(name, null);
            validateValue(name, resolvedValue, meta);

            values[parameter.index()] = resolvedValue;
        }

        @SuppressWarnings("unchecked")
        T instance = (T) constructor.invoke(values);

        return populate(instance, true);
    }

    public T populate(T instance) throws Throwable {
        return populate(instance, false);
    }

    private T populate(T instance, boolean skipParams) throws Throwable {
        nonNull(instance, "instance");

        if (!classModel.clazz().isInstance(instance)) {
            throw new IllegalStateException(format(
                    "Expected instance of %s, but found %s",
                    classModel.clazz().getName(),
                    instance.getClass().getName()
            ));
        }

        for (PropertyModel property : classModel.properties()) {
            if (skipParams && property.parameter() != null) {
                continue;
            }

            String name = property.name();
            PropertyMeta meta = property.meta();

            boolean canMutate = meta != null && meta.mutate() && property.getter() != null;

            if (!canMutate && property.setter() == null) {
                continue;
            }

            PropertyValue<?> value = getValue(property);
            if (value == null) {
                validateValue(name, null, meta);
                continue;
            }

            if (!canMutate) {
                Object resolvedValue = value.getSafe(name, instance);
                validateValue(name, resolvedValue, meta);

                property.setter().set(instance, resolvedValue);
                continue;
            }

            Object currentValue = property.getter().get(instance);

            if (value instanceof PropertyValue.Mutable) {
                //noinspection unchecked,rawtypes
                ((PropertyValue.Mutable) value).mutateSafe(name, instance, currentValue);
                continue;
            }

            if (currentValue instanceof Collection || currentValue instanceof Map) {
                Object resolvedValue = value.getSafe(name, instance);
                if (resolvedValue == null) {
                    validateValue(name, null, meta);
                    continue;
                }

                if (currentValue instanceof Collection) {
                    //noinspection unchecked,rawtypes
                    ((Collection) currentValue).addAll(((Collection) resolvedValue));
                } else {
                    //noinspection unchecked,rawtypes
                    ((Map) currentValue).putAll(((Map) resolvedValue));
                }

                continue;
            }

            throw new IllegalStateException("Expected property '" + name + "' to be a mutable value");
        }

        return instance;
    }

    private @Nullable PropertyValue<?> getValue(PropertyModel property) {
        return properties.get(property);
    }

    private @Nullable PropertyValue<?> getValue(ParameterModel parameter) {
        PropertyKind<?> kind = parameter.meta().kind();
        PropertyModel property;

        if (kind == PropertyKind.PROPERTY) {
            property = classModel.properties().get(parameter.name());
        } else {
            property = classModel.properties().get(kind);
        }

        if (property == null) {
            throw new IllegalStateException(format(
                    "No property of kind %s found",
                    kind
            ));
        }

        return properties.get(property);
    }

    private void validateValue(String name, @Nullable Object value, @Nullable PropertyMeta meta) {
        if (value == null && meta != null && meta.required()) {
            throw new IllegalStateException(format(
                    "Required property %s is missing value",
                    name
            ));
        }
    }

    public static final class Builder<T> {
        private final ClassModel<T> classModel;
        private final Map<PropertyModel, PropertyValue<?>> properties;

        private Builder(ClassModel<T> classModel) {
            this.classModel = nonNull(classModel, "classModel");
            this.properties = new HashMap<>(classModel.properties().size());
        }

        public Builder<T> property(PropertyModel property, @Nullable Object value) {
            nonNull(property, "property");

            return property(property, PropertyValue.of(value));
        }

        public Builder<T> property(PropertyModel property, PropertyValue<?> value) {
            nonNull(property, "property");
            nonNull(value, "value");

            if (properties.containsKey(property)) {
                throw new IllegalArgumentException("Property " + property.name() + " is already set");
            }

            properties.put(property, value);
            return this;
        }

        public InstanceCreator<T> build() {
            return new InstanceCreator<>(this);
        }
    }
}
