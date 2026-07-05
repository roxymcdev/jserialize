package net.roxymc.jserialize.model.resolver;

import net.roxymc.jserialize.annotation.Creator;
import net.roxymc.jserialize.model.constructor.ConstructorModel;
import net.roxymc.jserialize.util.PropertyUtils;
import net.roxymc.jserialize.util.RecordUtils;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public class SimpleConstructorResolver implements ConstructorResolver {
    public static final ConstructorResolver INSTANCE = new SimpleConstructorResolver();

    protected SimpleConstructorResolver() {
    }

    @Override
    public void resolveConstructor(Class<?> clazz, ConstructorModel.Builder constructor) {
        Set<Executable> constructors = new HashSet<>();

        Constructor<?> primaryCtor = null;

        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            constructors.add(ctor);

            if (RecordUtils.isPrimaryConstructor(ctor)) {
                primaryCtor = ctor;
            }
        }

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

        Executable target = primaryCtor;

        for (Executable ctor : constructors) {
            if (target == ctor || ctor.getDeclaredAnnotation(Creator.class) == null) {
                continue;
            }

            if (target != null && target.getDeclaredAnnotation(Creator.class) != null) {
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

    protected void processParameters(Executable ctor, ConstructorModel.Builder builder) {
        AnnotatedType[] componentTypes = null;
        if (ctor instanceof Constructor && RecordUtils.isPrimaryConstructor((Constructor<?>) ctor)) {
            componentTypes = RecordUtils.getComponentTypes(ctor.getDeclaringClass());
        }

        Parameter[] parameters = ctor.getParameters();

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
