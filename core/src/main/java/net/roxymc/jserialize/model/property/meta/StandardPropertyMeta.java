package net.roxymc.jserialize.model.property.meta;

import net.roxymc.jserialize.annotation.Property;
import org.jspecify.annotations.Nullable;

final class StandardPropertyMeta extends AbstractPropertyMeta<Property> {
    StandardPropertyMeta(@Nullable Property annotation) {
        super(annotation);
    }

    @Override
    public PropertyKind<Property> kind() {
        return PropertyKind.PROPERTY;
    }

    @Override
    public boolean required() {
        return get(Property::required, false);
    }

    @Override
    public boolean writeNull() {
        return get(Property::writeNull, false);
    }

    @Override
    public boolean mutate() {
        return get(Property::mutate, false);
    }
}
