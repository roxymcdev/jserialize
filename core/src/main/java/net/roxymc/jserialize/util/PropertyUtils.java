package net.roxymc.jserialize.util;

import net.roxymc.jserialize.annotation.ExtraProperties;
import net.roxymc.jserialize.annotation.Id;
import net.roxymc.jserialize.annotation.Parent;
import net.roxymc.jserialize.annotation.Property;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Supplier;

public final class PropertyUtils {
    public static final Set<Class<? extends Annotation>> PROPERTY_ANNOTATIONS = Set.of(
            ExtraProperties.class, Id.class, Parent.class, Property.class
    );

    private PropertyUtils() {
    }

    public static boolean hasPropertyAnnotation(AnnotatedElement element) {
        for (Class<? extends Annotation> annotationType : PROPERTY_ANNOTATIONS) {
            if (element.isAnnotationPresent(annotationType)) {
                return true;
            }
        }

        return false;
    }

    public static @Nullable Annotation getPropertyAnnotation(AnnotatedElement element) {
        Set<Annotation> annotations = new LinkedHashSet<>();

        for (Annotation annotation : element.getAnnotations()) {
            if (PROPERTY_ANNOTATIONS.contains(annotation.annotationType())) {
                annotations.add(annotation);
            }
        }

        if (annotations.isEmpty()) {
            return null;
        }

        if (annotations.size() > 1) {
            StringJoiner message = new StringJoiner(", ", "Expected a single property annotation, but found: ", "");

            annotations.forEach(annotation -> message.add(
                    "@" + annotation.annotationType().getSimpleName()
            ));

            throw new IllegalStateException(message.toString());
        }

        return annotations.iterator().next();
    }

    public static String getPropertyName(AnnotatedElement element, Supplier<String> defaultName) {
        Annotation annotation = getPropertyAnnotation(element);
        if (annotation == null) {
            return defaultName.get();
        }

        String name = "";

        if (annotation instanceof ExtraProperties) {
            name = ((ExtraProperties) annotation).value();
        } else if (annotation instanceof Id) {
            name = ((Id) annotation).value();
        } else if (annotation instanceof Parent) {
            name = ((Parent) annotation).value();
        } else if (annotation instanceof Property) {
            name = ((Property) annotation).value();
        }

        return !name.isEmpty() ? name : defaultName.get();
    }
}
