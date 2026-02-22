package net.roxymc.jserialize.model.constructor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class ConstructorModelImpl implements ConstructorModel {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final MethodHandle constructor;
    private final ParameterModel[] parameters;

    private ConstructorModelImpl(BuilderImpl builder) throws IllegalAccessException {
        this.parameters = builder.parameters.values().toArray(ParameterModel[]::new);

        MethodHandle constructor;

        if (builder.constructor instanceof Constructor<?>) {
            constructor = LOOKUP.unreflectConstructor((Constructor<?>) builder.constructor);
        } else {
            constructor = LOOKUP.unreflect((Method) builder.constructor);
        }

        this.constructor = constructor.asSpreader(Object[].class, parameters.length);
    }

    @Override
    public MethodHandle constructor() {
        return constructor;
    }

    @Override
    public ParameterModel[] parameters() {
        return parameters.clone();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "ConstructorModelImpl[", "]")
                .add("constructor=" + constructor)
                .add("parameters=" + Arrays.toString(parameters))
                .toString();
    }

    static final class BuilderImpl implements Builder {
        private final Executable constructor;
        private final Map<String, ParameterModel> parameters = new LinkedHashMap<>();
        private int parameterIndex = 0;

        BuilderImpl(Executable constructor) {
            this.constructor = nonNull(constructor, "constructor");
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
        public ConstructorModel build() throws IllegalAccessException {
            return new ConstructorModelImpl(this);
        }
    }
}
