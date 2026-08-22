package net.roxymc.jserialize.util;

import net.roxymc.jserialize.model.property.PropertyLikeModel;
import net.roxymc.jserialize.model.property.meta.PropertyKind;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringJoiner;

public final class PropertyUtils {
    private PropertyUtils() {
    }

    public static boolean hasPropertyAnnotation(AnnotatedElement element) {
        for (PropertyKind<?> kind : PropertyKind.values()) {
            if (element.isAnnotationPresent(kind.annotationType())) {
                return true;
            }
        }

        return false;
    }

    public static @Nullable Annotation getPropertyAnnotation(AnnotatedElement element) {
        Set<Annotation> annotations = new LinkedHashSet<>();

        for (PropertyKind<?> kind : PropertyKind.values()) {
            Annotation annotation = element.getAnnotation(kind.annotationType());

            if (annotation != null) {
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

    public static @Nullable String getPropertyName(AnnotatedElement element) {
        Annotation annotation = getPropertyAnnotation(element);
        if (annotation == null) {
            return null;
        }

        String name = PropertyKind.<Annotation>get(annotation.annotationType()).resolveName(annotation);
        return !name.isEmpty() ? name : null;
    }

    @Contract("_, !null -> !null")
    public static @Nullable String getPropertyName(AnnotatedElement element, @Nullable String fallbackName) {
        String name = getPropertyName(element);
        return name != null ? name : fallbackName;
    }

    public static String getDebugName(PropertyLikeModel property) {
        if (property.name() != null) {
            return property.name();
        }

        return "<unnamed @" + property.kind().annotationType().getSimpleName() + ">";
    }
}
