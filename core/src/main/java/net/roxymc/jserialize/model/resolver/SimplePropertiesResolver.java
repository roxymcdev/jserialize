package net.roxymc.jserialize.model.resolver;

import net.roxymc.jserialize.annotation.JSerializable;
import net.roxymc.jserialize.annotation.JSerializable.FieldResolution;
import net.roxymc.jserialize.annotation.JSerializable.MethodResolution;
import net.roxymc.jserialize.annotation.Transient;
import net.roxymc.jserialize.model.constructor.ConstructorModel;
import net.roxymc.jserialize.model.constructor.ParameterModel;
import net.roxymc.jserialize.model.property.PropertyMap;
import net.roxymc.jserialize.util.PropertyUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;
import static net.roxymc.jserialize.util.PropertyUtils.hasPropertyAnnotation;
import static net.roxymc.jserialize.util.StringUtils.decapitalize;
import static net.roxymc.jserialize.util.StringUtils.hasPrefix;

public class SimplePropertiesResolver implements PropertiesResolver {
    public static final SimplePropertiesResolver INSTANCE = new SimplePropertiesResolver();

    protected static final Set<MethodSignature> OBJECT_METHODS_SIGNATURES = Arrays.stream(Object.class.getDeclaredMethods())
            .map(MethodSignature::new)
            .collect(Collectors.toUnmodifiableSet());

    protected final FieldResolution fieldResolution;
    protected final MethodResolution methodResolution;

    protected SimplePropertiesResolver() {
        this(FieldResolution.ALWAYS, MethodResolution.BEAN);
    }

    protected SimplePropertiesResolver(FieldResolution fieldResolution, MethodResolution methodResolution) {
        this.fieldResolution = fieldResolution;
        this.methodResolution = methodResolution;
    }

    protected SimplePropertiesResolver createResolver(FieldResolution fieldResolution, MethodResolution methodResolutions) {
        return new SimplePropertiesResolver(fieldResolution, methodResolutions);
    }

    protected Context createContext(PropertyMap.Builder properties) {
        return new Context(properties);
    }

    @Override
    public void resolveProperties(Class<?> clazz, PropertyMap.Builder properties) {
        processClass(clazz, createContext(properties));
    }

    @Override
    public void resolveProperties(ConstructorModel constructor, PropertyMap.Builder properties) {
        processParameters(constructor.parameters(), properties);
    }

    protected void processClass(Class<?> clazz, Context ctx) {
        SimplePropertiesResolver resolver = this;

        JSerializable annotation = clazz.getDeclaredAnnotation(JSerializable.class);
        if (annotation != null) {
            resolver = createResolver(annotation.fields(), annotation.methods());
        }

        resolver.processMembers(clazz, ctx);

        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            processClass(superclass, ctx);
        }

        for (Class<?> iface : clazz.getInterfaces()) {
            if (!ctx.seenInterfaces.add(iface)) {
                continue;
            }

            processClass(iface, ctx);
        }
    }

    protected void processMembers(Class<?> clazz, Context ctx) {
        processFields(clazz.getDeclaredFields(), ctx);
        processMethods(clazz.getDeclaredMethods(), ctx);
    }

    protected <M extends Member & AnnotatedElement> boolean shouldIgnore(M member) {
        return member.isSynthetic() || isStatic(member.getModifiers()) || isTransient(member.getModifiers()) || member.isAnnotationPresent(Transient.class);
    }

    protected void processFields(Field[] fields, Context ctx) {
        if (fieldResolution == FieldResolution.NEVER) {
            return;
        }

        for (Field field : fields) {
            if (shouldIgnore(field)) {
                continue;
            }

            if (fieldResolution == FieldResolution.ANNOTATED_ONLY && !hasPropertyAnnotation(field)) {
                continue;
            }

            processField(field, ctx);
        }
    }

    protected void processField(Field field, Context ctx) {
        String name = PropertyUtils.getPropertyName(field, field::getName);

        ctx.properties.withProperty(name, property -> property
                .field(field)
        );
    }

    protected void processMethods(Method[] methods, Context ctx) {
        if (methodResolution == MethodResolution.NEVER) {
            return;
        }

        for (Method method : methods) {
            if (shouldIgnore(method)) {
                continue;
            }

            if (methodResolution == MethodResolution.ANNOTATED_ONLY && !hasPropertyAnnotation(method)) {
                continue;
            }

            if (!ctx.seenMethods.add(new MethodSignature(method))) {
                continue;
            }

            processMethod(method, ctx);
        }
    }

    protected void processMethod(Method method, Context ctx) {
        String name = getPropertyName(method);
        if (name == null) {
            return;
        }

        ctx.properties.withProperty(name, property -> {
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

    protected @Nullable String getPropertyName(Method method) {
        String propertyName = PropertyUtils.getPropertyName(method);
        if (propertyName != null) {
            return propertyName;
        }

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

        return methodResolution != MethodResolution.BEAN ? methodName : null;
    }

    protected void processParameters(ParameterModel[] parameters, PropertyMap.Builder properties) {
        for (ParameterModel parameter : parameters) {
            if (parameter.implicit()) {
                continue;
            }

            properties.withProperty(parameter.meta().kind(), parameter.name(), property -> property
                    .parameter(parameter)
            );
        }
    }

    protected static class Context {
        protected final Set<MethodSignature> seenMethods = new HashSet<>(OBJECT_METHODS_SIGNATURES);
        protected final Set<Class<?>> seenInterfaces = new HashSet<>();

        protected final PropertyMap.Builder properties;

        protected Context(PropertyMap.Builder properties) {
            this.properties = properties;
        }
    }

    protected static final class MethodSignature {
        private final String name;
        private final List<Class<?>> parameters;

        protected MethodSignature(Method method) {
            this.name = method.getName();
            this.parameters = List.of(method.getParameterTypes());
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, parameters);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (!(obj instanceof MethodSignature)) {
                return false;
            }

            MethodSignature that = (MethodSignature) obj;
            return Objects.equals(this.name, that.name) && Objects.equals(this.parameters, that.parameters);
        }
    }
}
