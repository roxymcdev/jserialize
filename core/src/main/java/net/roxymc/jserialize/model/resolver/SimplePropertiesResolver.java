package net.roxymc.jserialize.model.resolver;

import net.roxymc.jserialize.annotation.*;
import net.roxymc.jserialize.model.constructor.ConstructorModel;
import net.roxymc.jserialize.model.constructor.ParameterModel;
import net.roxymc.jserialize.model.property.PropertyMap;
import net.roxymc.jserialize.util.PropertyResolution;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Set;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static net.roxymc.jserialize.util.StringUtils.decapitalize;
import static net.roxymc.jserialize.util.StringUtils.hasPrefix;

public class SimplePropertiesResolver implements PropertiesResolver {
    public static final SimplePropertiesResolver INSTANCE = new SimplePropertiesResolver();
    protected static final Set<Class<? extends Annotation>> PROPERTY_ANNOTATIONS = Set.of(
            Id.class, Property.class, ExtraProperties.class
    );

    private final PropertyResolution fieldResolution, methodResolution;

    protected SimplePropertiesResolver() {
        this(PropertyResolution.ALWAYS, PropertyResolution.ALWAYS);
    }

    protected SimplePropertiesResolver(PropertyResolution fieldResolution, PropertyResolution methodResolution) {
        this.fieldResolution = fieldResolution;
        this.methodResolution = methodResolution;
    }

    @Override
    public PropertyMap resolveProperties(Class<?> clazz, @Nullable ConstructorModel constructor) throws IllegalAccessException {
        PropertyMap.Builder properties = PropertyMap.builder();

        while (clazz != Object.class) {
            SimplePropertiesResolver resolver = this;

            JSerializable annotation = clazz.getDeclaredAnnotation(JSerializable.class);
            if (annotation != null) {
                resolver = new SimplePropertiesResolver(annotation.fields(), annotation.methods());
            }

            resolver.resolveProperties(clazz, properties);

            clazz = clazz.getSuperclass();
        }

        if (constructor != null) {
            processParameters(constructor.parameters(), properties);
        }

        return properties.build();
    }

    protected void resolveProperties(Class<?> clazz, PropertyMap.Builder properties) {
        processFields(clazz.getDeclaredFields(), properties);
        processMethods(clazz.getDeclaredMethods(), properties);
    }

    protected <M extends Member & AnnotatedElement> boolean shouldIgnore(M member) {
        return member.isSynthetic() || isStatic(member.getModifiers()) || isTransient(member.getModifiers()) || member.isAnnotationPresent(Transient.class);
    }

    protected @Nullable String resolveAnnotatedName(AnnotatedElement element) {
        Property property = element.getAnnotation(Property.class);
        ExtraProperties extraProperties = element.getAnnotation(ExtraProperties.class);

        if (property != null && extraProperties != null) {
            throw new IllegalStateException("@Property and @ExtraProperties cannot be used with each other");
        }

        if (property != null && !property.value().isEmpty()) {
            return property.value();
        }

        if (extraProperties != null && !extraProperties.value().isEmpty()) {
            return extraProperties.value();
        }

        return null;
    }

    protected void processFields(Field[] fields, PropertyMap.Builder properties) {
        if (fieldResolution == PropertyResolution.NEVER) {
            return;
        }

        for (Field field : fields) {
            if (shouldIgnore(field)) {
                continue;
            }

            if (fieldResolution == PropertyResolution.ANNOTATED_ONLY && PROPERTY_ANNOTATIONS.stream().noneMatch(field::isAnnotationPresent)) {
                continue;
            }

            processField(field, properties);
        }
    }

    protected void processField(Field field, PropertyMap.Builder properties) {
        field.setAccessible(true);

        String name = resolvePropertyName(field);

        properties.withProperty(name, property -> property
                .field(field)
        );
    }

    protected String resolvePropertyName(Field field) {
        return requireNonNullElse(resolveAnnotatedName(field), field.getName());
    }

    protected void processMethods(Method[] methods, PropertyMap.Builder properties) {
        if (methodResolution == PropertyResolution.NEVER) {
            return;
        }

        for (Method method : methods) {
            if (shouldIgnore(method)) {
                continue;
            }

            if (methodResolution == PropertyResolution.ANNOTATED_ONLY && PROPERTY_ANNOTATIONS.stream().noneMatch(method::isAnnotationPresent)) {
                continue;
            }

            processMethod(method, properties);
        }
    }

    protected void processMethod(Method method, PropertyMap.Builder properties) {
        method.setAccessible(true);

        String name = resolvePropertyName(method);

        properties.withProperty(name, property -> {
            switch (method.getParameterCount()) {
                case 0:
                    property.getter(method);
                    return;
                case 1:
                    property.setter(method);
                    return;
            }

            throw new IllegalStateException(format(
                    "Expected method %s in class %s to have either 0 or 1 parameters, but has %s!",
                    method.getName(),
                    method.getDeclaringClass().getName(),
                    method.getParameterCount()
            ));
        });
    }

    protected String resolvePropertyName(Method method) {
        return requireNonNullElseGet(resolveAnnotatedName(method), () -> {
            String methodName = method.getName();

            switch (method.getParameterCount()) {
                case 0:
                    if (hasPrefix(methodName, "get")) {
                        return decapitalize(methodName.substring(3));
                    } else if (hasPrefix(methodName, "is")) {
                        return decapitalize(methodName.substring(2));
                    }
                    break;
                case 1:
                    if (hasPrefix(methodName, "set")) {
                        return decapitalize(methodName.substring(3));
                    }
                    break;
            }

            return methodName;
        });
    }

    protected void processParameters(ParameterModel[] parameters, PropertyMap.Builder properties) {
        for (ParameterModel parameter : parameters) {
            if (parameter.implicit()) {
                continue;
            }

            properties.withProperty(parameter.name(), property -> property
                    .parameter(parameter)
            );
        }
    }
}
