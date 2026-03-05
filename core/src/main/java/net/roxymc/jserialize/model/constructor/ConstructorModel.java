package net.roxymc.jserialize.model.constructor;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

@ApiStatus.NonExtendable
public interface ConstructorModel {
    @ApiStatus.Internal
    static Builder builder() {
        return new ConstructorModelImpl.BuilderImpl();
    }

    Object invoke(@Nullable Object... args) throws Throwable;

    ParameterModel[] parameters();

    @ApiStatus.NonExtendable
    interface Builder {
        Builder executable(Executable executable);

        Builder parameter(String name, Parameter parameter);

        @ApiStatus.Internal
        ConstructorModel build(MethodHandles.Lookup methodLookup) throws IllegalAccessException;
    }
}
