package net.roxymc.jserialize.model.property.meta;

import net.roxymc.jserialize.annotation.JSerial.Property;
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
        return get(Property::required, Property.DEFAULT_REQUIRED);
    }

    @Override
    public boolean writeNull() {
        return get(Property::writeNull, Property.DEFAULT_WRITE_NULL);
    }

    @Override
    public boolean mutate() {
        return get(Property::mutate, Property.DEFAULT_MUTATE);
    }
}
