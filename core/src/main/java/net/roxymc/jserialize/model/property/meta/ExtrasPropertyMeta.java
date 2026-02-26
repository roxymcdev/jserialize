package net.roxymc.jserialize.model.property.meta;

import net.roxymc.jserialize.annotation.ExtraProperties;
import org.jspecify.annotations.Nullable;

final class ExtrasPropertyMeta extends AbstractPropertyMeta<ExtraProperties> {
    ExtrasPropertyMeta(@Nullable ExtraProperties annotation) {
        super(annotation);
    }

    @Override
    public PropertyKind<ExtraProperties> kind() {
        return PropertyKind.EXTRAS;
    }

    @Override
    public boolean required() {
        return false;
    }

    @Override
    public boolean writeNull() {
        return false;
    }

    @Override
    public boolean mutate() {
        return get(ExtraProperties::mutate, false);
    }
}
