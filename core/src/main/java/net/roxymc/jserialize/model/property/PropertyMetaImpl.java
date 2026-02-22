package net.roxymc.jserialize.model.property;

import net.roxymc.jserialize.annotation.ExtraProperties;
import net.roxymc.jserialize.annotation.Id;
import net.roxymc.jserialize.annotation.Parent;
import net.roxymc.jserialize.annotation.Property;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Parameter;
import java.util.StringJoiner;

final class PropertyMetaImpl implements PropertyMeta {
    private static final String INCOMPATIBLE_WITH_EXTRA_PROPERTIES = "@%s cannot be used alongside @ExtraProperties";

    private final boolean id;
    private final boolean required;
    private final boolean writeNull;
    private final boolean mutate;
    private final boolean extra;
    private final boolean injectParent;

    PropertyMetaImpl(AnnotatedElement element, boolean injectParent) {
        if (element instanceof Parameter) {
            checkUnsupported(element, Id.class, "@%s cannot be used on a parameter");
        }

        if (element.isAnnotationPresent(ExtraProperties.class)) {
            if (injectParent) {
                throw new IllegalStateException("@ExtraProperties cannot be used on an implicit parent parameter");
            }

            checkUnsupported(element, Property.class, INCOMPATIBLE_WITH_EXTRA_PROPERTIES);

            ExtraProperties extraProps = element.getAnnotation(ExtraProperties.class);

            this.id = checkUnsupported(element, Id.class, INCOMPATIBLE_WITH_EXTRA_PROPERTIES);
            this.required = false;
            this.writeNull = false;
            this.mutate = extraProps.mutate();
            this.extra = true;
            this.injectParent = checkUnsupported(element, Parent.class, INCOMPATIBLE_WITH_EXTRA_PROPERTIES);
        } else {
            Property property = element.getAnnotation(Property.class);

            this.id = element.isAnnotationPresent(Id.class);
            this.required = injectParent || (property != null && property.required());
            this.writeNull = property != null && property.writeNull();
            this.mutate = property != null && property.mutate();
            this.extra = false;
            this.injectParent = injectParent || element.isAnnotationPresent(Parent.class);
        }
    }

    private static boolean checkUnsupported(AnnotatedElement element, Class<? extends Annotation> annotation, String message) {
        if (element.isAnnotationPresent(annotation)) {
            throw new IllegalStateException(String.format(message, annotation.getSimpleName()));
        }

        return false;
    }

    @Override
    public boolean id() {
        return id;
    }

    @Override
    public boolean required() {
        return required;
    }

    @Override
    public boolean writeNull() {
        return writeNull;
    }

    @Override
    public boolean mutate() {
        return mutate;
    }

    @Override
    public boolean extra() {
        return extra;
    }

    @Override
    public boolean injectParent() {
        return injectParent;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "MetaImpl[", "]")
                .add("id=" + id)
                .add("required=" + required)
                .add("writeNull=" + writeNull)
                .add("mutate=" + mutate)
                .add("extra=" + extra)
                .add("injectParent=" + injectParent)
                .toString();
    }
}
