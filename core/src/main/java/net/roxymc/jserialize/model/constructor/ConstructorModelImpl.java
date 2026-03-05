package net.roxymc.jserialize.model.constructor;

import net.roxymc.jserialize.util.ObjectUtils;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class ConstructorModelImpl implements ConstructorModel {
    private final MethodHandle handle;
    private final ParameterModel[] parameters;

    private ConstructorModelImpl(BuilderImpl builder, MethodHandles.Lookup methodLookup) throws IllegalAccessException {
        this.parameters = builder.parameters.values().toArray(ParameterModel[]::new);

        Executable executable = nonNull(builder.executable, "executable");
        MethodHandle handle;

        if (executable instanceof Constructor<?>) {
            handle = methodLookup.unreflectConstructor((Constructor<?>) executable);
        } else {
            handle = methodLookup.unreflect((Method) executable);
        }

        this.handle = handle.asSpreader(Object[].class, parameters.length);
    }

    @Override
    public Object invoke(@Nullable Object... args) throws Throwable {
        return handle.invoke(args);
    }

    @Override
    public ParameterModel[] parameters() {
        return parameters.clone();
    }

    @Override
    public String toString() {
        return ObjectUtils.toString(this)
                .add("parameters=" + Arrays.toString(parameters))
                .toString();
    }

    static final class BuilderImpl implements Builder {
        private @Nullable Executable executable;
        private final Map<String, ParameterModel> parameters = new LinkedHashMap<>();
        private int parameterIndex = 0;

        @Override
        public Builder executable(Executable executable) {
            this.executable = nonNull(executable, "executable");
            return this;
        }

        @Override
        public Builder parameter(String name, Parameter parameter) {
            nonNull(name, "name");
            nonNull(parameter, "parameter");

            if (parameters.containsKey(name)) {
                throw new IllegalStateException("Parameter " + name + " already exists");
            }

            parameters.put(name, new ParameterModelImpl(name, parameterIndex++, parameter));
            return this;
        }

        @Override
        public ConstructorModel build(MethodHandles.Lookup methodLookup) throws IllegalAccessException {
            return new ConstructorModelImpl(this, methodLookup);
        }
    }
}
