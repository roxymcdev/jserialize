package net.roxymc.jserialize.model.constructor;

import net.roxymc.jserialize.util.ObjectUtils;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class ConstructorModelImpl implements ConstructorModel {
    private final MethodHandle handle;
    private final ParameterModel[] parameters;

    private ConstructorModelImpl(BuilderImpl builder, MethodHandles.Lookup methodLookup) throws IllegalAccessException {
        this.parameters = builder.parameters.toArray(ParameterModel[]::new);

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
        private final Set<ParameterModel> parameters = new LinkedHashSet<>();
        private final Map<String, ParameterModel> nameToParameter = new LinkedHashMap<>();
        private @Nullable Executable executable;

        @Override
        public Builder executable(Executable executable) {
            nonNull(executable, "executable");

            if (this.executable != null) {
                throw new IllegalStateException("Executable is already set");
            }

            this.executable = executable;
            return this;
        }

        @Override
        public Builder parameter(@Nullable String name, int index, Parameter parameter, AnnotatedType type) {
            nonNull(parameter, "parameter");

            if (nameToParameter.containsKey(name)) {
                throw new IllegalStateException("Parameter already exists: " + name);
            }

            ParameterModelImpl parameterModel = new ParameterModelImpl(name, index, parameter, type);

            if (name != null) {
                nameToParameter.put(name, parameterModel);
            }

            parameters.add(parameterModel);
            return this;
        }

        @Override
        public ConstructorModel build(MethodHandles.Lookup methodLookup) throws IllegalAccessException {
            return new ConstructorModelImpl(this, methodLookup);
        }
    }
}
