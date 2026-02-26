package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.model.constructor.ParameterModel;
import net.roxymc.jserialize.model.property.meta.PropertyKind;
import net.roxymc.jserialize.model.property.meta.PropertyMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

@ApiStatus.NonExtendable
public interface PropertyModel {
    String name();

    @Nullable MethodHandle getter();

    @Nullable Type getterType();

    @Nullable MethodHandle setter();

    @Nullable Type setterType();

    @Nullable ParameterModel parameter();

    default @Nullable Type parameterType() {
        ParameterModel parameter = parameter();
        return parameter != null ? parameter.type() : null;
    }

    default PropertyKind<?> kind() {
        PropertyMeta meta = meta();
        return meta != null ? meta.kind() : PropertyKind.PROPERTY;
    }

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
