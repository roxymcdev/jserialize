package net.roxymc.jserialize.model.resolver;

import net.roxymc.jserialize.annotation.Creator;
import net.roxymc.jserialize.annotation.ExtraProperties;
import net.roxymc.jserialize.annotation.Property;
import net.roxymc.jserialize.model.constructor.ConstructorModel;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SimpleConstructorResolver implements ConstructorResolver {
    public static final SimpleConstructorResolver INSTANCE = new SimpleConstructorResolver();

    protected SimpleConstructorResolver() {
    }

    @Override
    public @Nullable ConstructorModel resolveConstructor(Class<?> clazz) throws IllegalAccessException {
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
            throw new IllegalStateException("No constructors found");
        }

        Executable targetConstructor = null;

        for (Executable constructor : constructors) {
            if (constructor.getDeclaredAnnotation(Creator.class) == null) {
                continue;
            }

            if (targetConstructor != null) {
                throw new IllegalStateException("Multiple constructors found");
            }

            targetConstructor = constructor;
        }

        if (targetConstructor == null) {
            if (constructors.size() > 1) {
                throw new IllegalStateException("Multiple constructors found");
            }

            targetConstructor = constructors.iterator().next();
        }

        ConstructorModel.Builder builder = ConstructorModel.builder(targetConstructor);

        processParameters(targetConstructor.getParameters(), builder);

        return builder.build();
    }

    protected void processParameters(Parameter[] parameters, ConstructorModel.Builder builder) {
        for (Parameter parameter : parameters) {
            builder.parameter(resolvePropertyName(parameter), parameter);
        }
    }

    protected String resolvePropertyName(Parameter parameter) {
        Property property = parameter.getAnnotation(Property.class);
        ExtraProperties extraProperties = parameter.getAnnotation(ExtraProperties.class);

        if (property != null && extraProperties != null) {
            throw new IllegalStateException("@Property and @ExtraProperties cannot be used with each other");
        }

        if (property != null && !property.value().isEmpty()) {
            return property.value();
        }

        if (extraProperties != null && !extraProperties.value().isEmpty()) {
            return extraProperties.value();
        }

        if (!parameter.isNamePresent()) {
            throw new IllegalStateException("Parameter has no name");
        }

        return parameter.getName();
    }
}
