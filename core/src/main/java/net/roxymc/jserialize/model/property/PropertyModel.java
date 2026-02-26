package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.model.constructor.ParameterModel;
import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

@ApiStatus.NonExtendable
public interface PropertyModel {
    @Contract(pure = true)
    String name();

    @Contract(pure = true)
    @Nullable GetterRef getter();

    @Contract(pure = true)
    default @Nullable Type getterType() {
        MethodRef getter = getter();
        return getter != null ? getter.valueType() : null;
    }

    @Contract(pure = true)
    @Nullable SetterRef setter();

    @Contract(pure = true)
    default @Nullable Type setterType() {
        MethodRef setter = setter();
        return setter != null ? setter.valueType() : null;
    }

    @Contract(pure = true)
    @Nullable ParameterModel parameter();

    @Contract(pure = true)
    default @Nullable Type parameterType() {
        ParameterModel parameter = parameter();
        return parameter != null ? parameter.type() : null;
    }

    @Contract(pure = true)
    default PropertyKind<?> kind() {
        PropertyMeta meta = meta();
        return meta != null ? meta.kind() : PropertyKind.PROPERTY;
    }

    @Contract(pure = true)
    @Nullable PropertyMeta meta();

    @ApiStatus.NonExtendable
    interface Builder {
        Builder field(Field field);

        Builder getter(Method getter);

        Builder setter(Method setter);

        Builder parameter(ParameterModel parameter);

        PropertyModel build() throws IllegalAccessException;
    }
}
