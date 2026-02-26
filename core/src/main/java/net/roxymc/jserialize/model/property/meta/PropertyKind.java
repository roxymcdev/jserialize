package net.roxymc.jserialize.model.property.meta;

import net.roxymc.jserialize.annotation.ExtraProperties;
import net.roxymc.jserialize.annotation.Id;
import net.roxymc.jserialize.annotation.Parent;
import net.roxymc.jserialize.annotation.Property;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.function.Function;

public final class PropertyKind<A extends Annotation> {
    public static final PropertyKind<ExtraProperties> EXTRAS = new PropertyKind<>(ExtraProperties.class, ExtrasPropertyMeta::new);
    public static final PropertyKind<Id> ID = new PropertyKind<>(Id.class, IdPropertyMeta::new);
    public static final PropertyKind<Parent> PARENT = new PropertyKind<>(Parent.class, ParentPropertyMeta::new);
    public static final PropertyKind<Property> PROPERTY = new PropertyKind<>(Property.class, StandardPropertyMeta::new);

    private final Class<A> annotationType;
    private final Function<@Nullable A, PropertyMeta> metaFactory;

    private PropertyKind(Class<A> annotationType, Function<@Nullable A, PropertyMeta> metaFactory) {
        this.annotationType = annotationType;
        this.metaFactory = metaFactory;
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> PropertyKind<A> get(@Nullable Class<? extends A> annotation) {
        if (annotation == ExtraProperties.class) {
            return (PropertyKind<A>) EXTRAS;
        } else if (annotation == Id.class) {
            return (PropertyKind<A>) ID;
        } else if (annotation == Parent.class) {
            return (PropertyKind<A>) PARENT;
        } else if (annotation == Property.class || annotation == null) {
            return (PropertyKind<A>) PROPERTY;
        }

        throw new AssertionError("Unknown property annotation: " + annotation.getName());
    }

    public PropertyMeta createMeta(@Nullable A annotation) {
        return metaFactory.apply(annotation);
    }

    @Override
    public String toString() {
        return "PropertyKind[" + annotationType.getSimpleName() + "]";
    }
}
