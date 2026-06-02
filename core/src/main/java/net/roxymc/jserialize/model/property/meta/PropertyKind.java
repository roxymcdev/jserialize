package net.roxymc.jserialize.model.property.meta;

import net.roxymc.jserialize.annotation.*;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;

import static net.roxymc.jserialize.model.property.meta.PropertyKind.Flag.DONT_DESERIALIZE;
import static net.roxymc.jserialize.model.property.meta.PropertyKind.Flag.DONT_SERIALIZE;

public final class PropertyKind<A extends Annotation> {
    private static final Map<Class<? extends Annotation>, PropertyKind<?>> PROPERTY_KINDS = new HashMap<>();

    public static final PropertyKind<ExtraProperties> EXTRAS = register(ExtraProperties.class, ExtraProperties::value, ExtrasPropertyMeta::new, DONT_DESERIALIZE);
    public static final PropertyKind<Id> ID = register(Id.class, Id::value, IdPropertyMeta::new);
    public static final PropertyKind<Key> KEY = register(Key.class, Key::value, KeyPropertyMeta::new, DONT_SERIALIZE);
    public static final PropertyKind<Parent> PARENT = register(Parent.class, Parent::value, ParentPropertyMeta::new, DONT_SERIALIZE);
    public static final PropertyKind<Property> PROPERTY = register(Property.class, Property::value, StandardPropertyMeta::new);

    private final Class<A> annotationType;
    private final Function<A, String> nameResolver;
    private final Function<@Nullable A, PropertyMeta> metaFactory;
    private final Set<Flag> flags;

    private PropertyKind(
            Class<A> annotationType,
            Function<A, String> nameResolver,
            Function<@Nullable A, PropertyMeta> metaFactory,
            Flag[] flags
    ) {
        this.annotationType = annotationType;
        this.metaFactory = metaFactory;
        this.nameResolver = nameResolver;
        this.flags = flags.length == 0 ? Collections.emptySet() : Collections.unmodifiableSet(EnumSet.of(flags[0], flags));
    }

    private static <A extends Annotation> PropertyKind<A> register(
            Class<A> annotationType,
            Function<A, String> nameResolver,
            Function<@Nullable A, PropertyMeta> metaFactory,
            Flag... flags
    ) {
        @SuppressWarnings("unchecked")
        PropertyKind<A> kind = (PropertyKind<A>) PROPERTY_KINDS.compute(annotationType, ($, registered) -> {
            if (registered != null) {
                throw new IllegalStateException("Property kind of type " + annotationType.getSimpleName() + " is already registered");
            }

            return new PropertyKind<>(annotationType, nameResolver, metaFactory, flags);
        });
        return kind;
    }

    public static PropertyKind<?>[] values() {
        return PROPERTY_KINDS.values().toArray(PropertyKind[]::new);
    }

    public static <A extends Annotation> PropertyKind<A> get(@Nullable Class<? extends A> annotation) {
        if (annotation == null) {
            @SuppressWarnings("unchecked")
            PropertyKind<A> kind = (PropertyKind<A>) PropertyKind.PROPERTY;
            return kind;
        }

        @SuppressWarnings("unchecked")
        PropertyKind<A> kind = (PropertyKind<A>) PROPERTY_KINDS.get(annotation);
        if (kind != null) {
            return kind;
        }

        throw new AssertionError("Unknown property annotation: " + annotation.getName());
    }

    public Class<A> annotationType() {
        return annotationType;
    }

    public String resolveName(A annotation) {
        return nameResolver.apply(annotation);
    }

    public PropertyMeta createMeta(@Nullable A annotation) {
        return metaFactory.apply(annotation);
    }

    public boolean shouldSerialize() {
        return !flags.contains(DONT_SERIALIZE);
    }

    public boolean shouldDeserialize() {
        return shouldSerialize() && !flags.contains(DONT_DESERIALIZE);
    }

    @Override
    public String toString() {
        return "PropertyKind[" + annotationType.getSimpleName() + "]";
    }

    enum Flag {
        DONT_SERIALIZE,
        DONT_DESERIALIZE,
    }
}
