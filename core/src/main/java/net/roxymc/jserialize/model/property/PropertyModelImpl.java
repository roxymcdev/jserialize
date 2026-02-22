package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.model.constructor.ParameterModel;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.StringJoiner;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;
import static net.roxymc.jserialize.util.TypeUtils.rawType;

final class PropertyModelImpl implements PropertyModel {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final String name;
    private final @Nullable MethodHandle getter, setter;
    private final @Nullable Type getterType, setterType;
    private final @Nullable ParameterModel parameter;
    private final @Nullable PropertyMeta meta;

    private PropertyModelImpl(BuilderImpl builder) throws IllegalAccessException {
        this.name = builder.name;

        PropertyMeta meta = null;

        if (builder.getter != null) {
            this.getter = LOOKUP.unreflect(builder.getter);
            this.getterType = builder.getter.getGenericReturnType();
            meta = PropertyMeta.of(builder.getter);
        } else if (builder.field != null) {
            this.getter = LOOKUP.unreflectGetter(builder.field);
            this.getterType = builder.field.getGenericType();
            meta = PropertyMeta.of(builder.field);
        } else {
            this.getter = null;
            this.getterType = null;
        }

        if (builder.setter != null) {
            this.setter = LOOKUP.unreflect(builder.setter);
            this.setterType = builder.setter.getGenericParameterTypes()[0];

            if (meta == null) {
                meta = PropertyMeta.of(builder.setter);
            }
        } else if (builder.field != null && !Modifier.isFinal(builder.field.getModifiers())) {
            this.setter = LOOKUP.unreflectSetter(builder.field);
            this.setterType = builder.field.getGenericType();
        } else {
            this.setter = null;
            this.setterType = null;
        }

        this.parameter = builder.parameter;
        this.meta = meta;

        if (meta != null && meta.extra()) {
            if (getterType != null && !Map.class.isAssignableFrom(rawType(getterType))) {
                throw new IllegalStateException("@ExtraProperties property getter type must assignable to Map");
            }

            if (setterType != null && !Map.class.isAssignableFrom(rawType(setterType))) {
                throw new IllegalStateException("@ExtraProperties property setter type must assignable to Map");
            }
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public @Nullable MethodHandle getter() {
        return getter;
    }

    @Override
    public @Nullable Type getterType() {
        return getterType;
    }

    @Override
    public @Nullable MethodHandle setter() {
        return setter;
    }

    @Override
    public @Nullable Type setterType() {
        return setterType;
    }

    @Override
    public @Nullable ParameterModel parameter() {
        return parameter;
    }

    @Override
    public @Nullable PropertyMeta meta() {
        return meta;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "PropertyModelImpl[", "]")
                .add("name='" + name + "'")
                .add("getter=" + getter)
                .add("setter=" + setter)
                .add("parameterIndex=" + parameter)
                .add("meta=" + meta)
                .toString();
    }

    static final class BuilderImpl implements Builder {
        private final String name;
        private @Nullable Field field;
        private @Nullable Method getter, setter;
        private @Nullable ParameterModel parameter;

        BuilderImpl(String name) {
            this.name = nonNull(name, "name");
        }

        @Override
        public Builder field(Field field) {
            if (this.field != null) {
                throw new IllegalStateException("field is already set");
            }

            this.field = nonNull(field, "field");
            return this;
        }

        @Override
        public Builder getter(Method getter) {
            if (this.getter != null) {
                throw new IllegalStateException("getter is already set");
            }

            this.getter = nonNull(getter, "getter");
            return this;
        }

        @Override
        public Builder setter(Method setter) {
            if (this.setter != null) {
                throw new IllegalStateException("setter is already set");
            }

            this.setter = nonNull(setter, "setter");
            return this;
        }

        @Override
        public Builder parameter(ParameterModel parameter) {
            if (this.parameter != null) {
                throw new IllegalStateException("parameter is already set");
            }

            this.parameter = parameter;
            return this;
        }

        @Override
        public PropertyModel build() throws IllegalAccessException {
            return new PropertyModelImpl(this);
        }
    }
}
