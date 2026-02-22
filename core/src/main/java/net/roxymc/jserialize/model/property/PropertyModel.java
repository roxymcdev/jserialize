package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.model.constructor.ParameterModel;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

@ApiStatus.NonExtendable
public interface PropertyModel {
    static Builder builder(String name) {
        return new PropertyModelImpl.BuilderImpl(name);
    }

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
