package net.roxymc.jserialize.model.property.meta;

import net.roxymc.jserialize.annotation.JSerial.Extra;
import org.jspecify.annotations.Nullable;

final class ExtraPropertyMeta extends AbstractPropertyMeta<Extra> {
    ExtraPropertyMeta(@Nullable Extra annotation) {
        super(annotation);
    }

    @Override
    public PropertyKind<Extra> kind() {
        return PropertyKind.EXTRA;
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
        return get(Extra::mutate, Extra.DEFAULT_MUTATE);
    }
}
