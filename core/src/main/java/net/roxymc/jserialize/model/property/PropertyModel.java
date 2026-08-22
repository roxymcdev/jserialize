package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.model.constructor.ParameterModel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@ApiStatus.NonExtendable
public interface PropertyModel extends PropertyLikeModel {
    @Contract(pure = true)
    @Nullable GetterRef getter();

    @Contract(pure = true)
    default @Nullable AnnotatedType getterType() {
        MethodRef getter = getter();
        return getter != null ? getter.valueType() : null;
    }

    @Contract(pure = true)
    @Nullable SetterRef setter();

    @Contract(pure = true)
    default @Nullable AnnotatedType setterType() {
        MethodRef setter = setter();
        return setter != null ? setter.valueType() : null;
    }

    @Contract(pure = true)
    @Nullable ParameterModel parameter();

    @Contract(pure = true)
    default @Nullable AnnotatedType parameterType() {
        ParameterModel parameter = parameter();
        return parameter != null ? parameter.type() : null;
    }

    @ApiStatus.NonExtendable
    interface Builder {
        Builder field(Field field);

        Builder getter(Method getter);

        Builder setter(Method setter);

        Builder parameter(ParameterModel parameter);
    }
}
