package net.roxymc.jserialize.model.constructor;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

@ApiStatus.NonExtendable
public interface ConstructorModel {
    static Builder builder(Executable constructor) {
        return new ConstructorModelImpl.BuilderImpl(constructor);
    }

    Object invoke(@Nullable Object... args) throws Throwable;

    ParameterModel[] parameters();

    @ApiStatus.NonExtendable
    interface Builder {
        Builder parameter(String name, Parameter parameter);

        ConstructorModel build() throws IllegalAccessException;
    }
}
