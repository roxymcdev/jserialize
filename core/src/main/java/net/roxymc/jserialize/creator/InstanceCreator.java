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
    private final Map<String, PropertyValue<?>> properties;
    private final @Nullable Object parent;

    private InstanceCreator(Builder<T> builder) {
        this.classModel = builder.classModel;
        this.properties = Map.copyOf(builder.properties);
        this.parent = builder.parent;
    }

    public static <T> Builder<T> builder(ClassModel<T> classModel) {
        return new Builder<>(classModel);
    }

    public T createInstance() throws Throwable {
        ConstructorModel constructor = classModel.constructor();

        if (constructor == null) {
            throw new IllegalStateException("No constructor found");
        }

        ParameterModel[] parameters = constructor.parameters();
        @Nullable Object[] values = new Object[parameters.length];

        for (ParameterModel parameter : parameters) {
            String name = parameter.name();
            PropertyMeta meta = parameter.meta();

            PropertyValue<?> lazyValue = getValue(name, meta.kind());
            if (lazyValue == null) {
                validateValue(name, null, meta);
                continue;
            }

            Object value = lazyValue.get(null, null);
            validateValue(name, value, meta);

            values[parameter.index()] = value;
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

        for (PropertyModel property : classModel.properties().values()) {
            if (skipParams && property.parameter() != null) {
                continue;
            }

            String name = property.name();
            PropertyMeta meta = property.meta();

            boolean canMutate = meta != null && meta.mutate() && property.getter() != null;

            if (!canMutate && property.setter() == null) {
                continue;
            }

            PropertyValue<?> lazyValue = getValue(name, property.kind());
            if (lazyValue == null) {
                validateValue(name, null, meta);
                continue;
            }

            if (!canMutate) {
                Object value = lazyValue.get(instance, null);
                validateValue(name, value, meta);

                property.setter().set(instance, value);
                continue;
            }

            Object currentValue = property.getter().get(instance);

            if (currentValue instanceof Collection<?> || currentValue instanceof Map<?, ?>) {
                Object value = lazyValue.get(instance, null);
                if (value == null) {
                    validateValue(name, null, meta);
                    continue;
                }

                if (currentValue instanceof Collection) {
                    //noinspection unchecked,rawtypes
                    ((Collection) currentValue).addAll(((Collection) value));
                } else {
                    //noinspection unchecked,rawtypes
                    ((Map) currentValue).putAll(((Map) value));
                }

                continue;
            }

            //noinspection unchecked,rawtypes
            ((PropertyValue) lazyValue).get(instance, currentValue);
        }

        return instance;
    }

    private @Nullable PropertyValue<?> getValue(String name, PropertyKind<?> kind) {
        if (kind == PropertyKind.PROPERTY) {
            return properties.get(name);
        } else if (kind == PropertyKind.PARENT) {
            return PropertyValue.of(parent);
        }

        PropertyModel property = classModel.properties().get(kind);
        if (property == null) {
            throw new IllegalStateException(format(
                    "No property of kind %s found",
                    kind
            ));
        }

        return properties.get(property.name());
    }

    private void validateValue(String name, @Nullable Object value, @Nullable PropertyMeta meta) {
        if (value == null && meta != null && meta.required()) {
            throw new IllegalStateException(format(
                    "Required property %s is missing a value",
                    name
            ));
        }
    }

    public static final class Builder<T> {
        private final ClassModel<T> classModel;
        private final Map<String, PropertyValue<?>> properties;
        private @Nullable Object parent;

        private Builder(ClassModel<T> classModel) {
            this.classModel = nonNull(classModel, "classModel");
            this.properties = new HashMap<>(classModel.properties().size());
        }

        public Builder<T> property(String name, @Nullable Object value) {
            nonNull(name, "name");

            return property(name, PropertyValue.of(value));
        }

        public Builder<T> property(String name, PropertyValue<?> value) {
            nonNull(name, "name");
            nonNull(value, "value");

            if (classModel.properties().get(name) == null) {
                throw new IllegalStateException("Property " + name + " does not exist");
            }

            if (properties.containsKey(name)) {
                throw new IllegalArgumentException("Property " + name + " already exists");
            }

            properties.put(name, value);
            return this;
        }

        public Builder<T> parent(@Nullable Object owner) {
            this.parent = owner;
            return this;
        }

        public InstanceCreator<T> build() {
            return new InstanceCreator<>(this);
        }
    }
}
