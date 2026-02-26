package net.roxymc.jserialize.model.property;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.model.constructor.ParameterModel;
import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import net.roxymc.jserialize.util.PropertyUtils;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.StringJoiner;

import static java.lang.String.format;
import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class PropertyModelImpl implements PropertyModel {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final String name;
    private final @Nullable GetterRef getter;
    private final @Nullable SetterRef setter;
    private final @Nullable ParameterModel parameter;
    private final @Nullable PropertyMeta meta;

    private PropertyModelImpl(BuilderImpl builder) throws IllegalAccessException {
        this.name = builder.name;

        PropertyMeta meta = null;

        if (builder.getter != null) {
            this.getter = new GetterRefImpl(
                    builder.getter.getGenericReturnType(),
                    builder.getter.getDeclaringClass(),
                    LOOKUP.unreflect(builder.getter)
            );

            meta = PropertyMeta.of(builder.getter);
        } else if (builder.field != null) {
            this.getter = new GetterRefImpl(
                    builder.field.getGenericType(),
                    builder.field.getDeclaringClass(),
                    LOOKUP.unreflectGetter(builder.field)
            );

            meta = PropertyMeta.of(builder.field);
        } else {
            this.getter = null;
        }

        if (builder.setter != null) {
            this.setter = new SetterRefImpl(
                    builder.setter.getGenericParameterTypes()[0],
                    builder.setter.getDeclaringClass(),
                    LOOKUP.unreflect(builder.setter)
            );

            if (meta == null) {
                meta = PropertyMeta.of(builder.setter);
            }
        } else if (builder.field != null && !Modifier.isFinal(builder.field.getModifiers())) {
            this.setter = new SetterRefImpl(
                    builder.field.getGenericType(),
                    builder.field.getDeclaringClass(),
                    LOOKUP.unreflectSetter(builder.field)
            );
        } else {
            this.setter = null;
        }

        this.parameter = builder.parameter;
        this.meta = meta;

        if (meta != null && meta.kind() == PropertyKind.EXTRAS) {
            if (getter != null && !Map.class.isAssignableFrom(GenericTypeReflector.erase(getter.valueType()))) {
                throw new IllegalStateException("@ExtraProperties property getter type must assignable to Map");
            }

            if (getter != null && !Map.class.isAssignableFrom(GenericTypeReflector.erase(getter.valueType()))) {
                throw new IllegalStateException("@ExtraProperties property setter type must assignable to Map");
            }
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public @Nullable GetterRef getter() {
        return getter;
    }

    @Override
    public @Nullable SetterRef setter() {
        return setter;
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
                .add("parameter=" + parameter)
                .add("meta=" + meta)
                .toString();
    }

    static final class BuilderImpl implements Builder {
        private final String name;
        private @Nullable Field field;
        private @Nullable Method getter, setter;
        private @Nullable ParameterModel parameter;
        @Nullable PropertyKind<?> kind;

        BuilderImpl(String name) {
            this.name = nonNull(name, "name");
        }

        private void checkKind(AnnotatedElement element) {
            Annotation annotation = PropertyUtils.getPropertyAnnotation(element);
            Class<? extends Annotation> annotationType = annotation != null ? annotation.annotationType() : null;

            checkKind(PropertyKind.get(annotationType));
        }

        private void checkKind(PropertyKind<?> kind) {
            if (this.kind == null) {
                this.kind = kind;
                return;
            }

            if (this.kind != kind) {
                throw new IllegalStateException(format(
                        "Expected kind: %s, but found: %s",
                        kind, this.kind
                ));
            }
        }

        @Override
        public Builder field(Field field) {
            if (this.field != null) {
                throw new IllegalStateException("field is already set");
            }

            checkKind(field);

            this.field = nonNull(field, "field");
            return this;
        }

        @Override
        public Builder getter(Method getter) {
            if (this.getter != null) {
                throw new IllegalStateException("getter is already set");
            }

            checkKind(getter);

            this.getter = nonNull(getter, "getter");
            return this;
        }

        @Override
        public Builder setter(Method setter) {
            if (this.setter != null) {
                throw new IllegalStateException("setter is already set");
            }

            checkKind(setter);

            this.setter = nonNull(setter, "setter");
            return this;
        }

        @Override
        public Builder parameter(ParameterModel parameter) {
            if (this.parameter != null) {
                throw new IllegalStateException("parameter is already set");
            }

            checkKind(parameter.meta().kind());

            this.parameter = parameter;
            return this;
        }

        @Override
        public PropertyModel build() throws IllegalAccessException {
            return new PropertyModelImpl(this);
        }
    }
}
