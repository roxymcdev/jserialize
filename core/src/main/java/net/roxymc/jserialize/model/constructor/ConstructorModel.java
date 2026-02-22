package net.roxymc.jserialize.model.constructor;

import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

@ApiStatus.NonExtendable
public interface ConstructorModel {
    static Builder builder(Executable constructor) {
        return new ConstructorModelImpl.BuilderImpl(constructor);
    }

    MethodHandle constructor();

    ParameterModel[] parameters();

    @ApiStatus.NonExtendable
    interface Builder {
        Builder parameter(String name, Parameter parameter);

        ConstructorModel build() throws IllegalAccessException;
    }
}
