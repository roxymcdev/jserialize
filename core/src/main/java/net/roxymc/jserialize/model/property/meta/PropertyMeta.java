package net.roxymc.jserialize.model.property.meta;

import net.roxymc.jserialize.util.PropertyUtils;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

@ApiStatus.NonExtendable
public interface PropertyMeta {
    static PropertyMeta of(AnnotatedElement element) {
        Annotation annotation = PropertyUtils.getPropertyAnnotation(element);
        Class<? extends Annotation> annotationType = annotation != null ? annotation.annotationType() : null;

        return PropertyKind.<Annotation>get(annotationType).createMeta(annotation);
    }

    PropertyKind<?> kind();

    boolean required();

    boolean writeNull();

    boolean mutate();

    default boolean shouldSerialize() {
        return !(kind() == PropertyKind.PARENT);
    }

    default boolean shouldDeserialize() {
        return shouldSerialize() && !(kind() == PropertyKind.EXTRAS);
    }
}
