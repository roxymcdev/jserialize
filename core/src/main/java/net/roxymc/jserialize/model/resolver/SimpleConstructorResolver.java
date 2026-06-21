package net.roxymc.jserialize.model.resolver;

import net.roxymc.jserialize.annotation.Creator;
import net.roxymc.jserialize.model.constructor.ConstructorModel;
import net.roxymc.jserialize.util.PropertyUtils;
import net.roxymc.jserialize.util.RecordUtils;

import java.lang.reflect.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SimpleConstructorResolver implements ConstructorResolver {
    public static final SimpleConstructorResolver INSTANCE = new SimpleConstructorResolver();

    protected SimpleConstructorResolver() {
    }

    @Override
    public void resolveConstructor(Class<?> clazz, ConstructorModel.Builder constructor) {
        Set<Executable> constructors = new HashSet<>();

        Collections.addAll(constructors, clazz.getDeclaredConstructors());

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isSynthetic() || !Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            if (method.getDeclaredAnnotation(Creator.class) != null) {
                constructors.add(method);
            }
        }

        if (constructors.isEmpty()) {
            throw new IllegalStateException("No constructor found");
        }

        Executable target = null;

        for (Executable ctor : constructors) {
            if (ctor.getDeclaredAnnotation(Creator.class) == null) {
                continue;
            }

            if (target != null) {
                throw new IllegalStateException("Multiple constructors found");
            }

            target = ctor;
        }

        if (target == null) {
            if (constructors.size() > 1) {
                throw new IllegalStateException("Multiple constructors found");
            }

            target = constructors.iterator().next();
        }

        constructor.executable(target);

        processParameters(target, constructor);
    }

    protected void processParameters(Executable executable, ConstructorModel.Builder builder) {
        Class<?> clazz = executable.getDeclaringClass();

        AnnotatedType[] componentTypes = null;
        if (executable instanceof Constructor && RecordUtils.isRecord(clazz) && RecordUtils.isPrimaryConstructor((Constructor<?>) executable)) {
            componentTypes = RecordUtils.getComponentTypes(clazz);
        }

        Parameter[] parameters = executable.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            String name = PropertyUtils.getPropertyName(parameter, () -> {
                if (!parameter.isNamePresent()) {
                    throw new IllegalStateException("Parameter has no name: " + parameter);
                }

                return parameter.getName();
            });

            AnnotatedType type = componentTypes != null ? componentTypes[i] : parameter.getAnnotatedType();

            builder.parameter(name, i, parameter, type);
        }
    }
}
