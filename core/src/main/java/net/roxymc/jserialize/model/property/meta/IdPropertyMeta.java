package net.roxymc.jserialize.model.property.meta;

import net.roxymc.jserialize.annotation.JSerial.Id;
import org.jspecify.annotations.Nullable;

final class IdPropertyMeta extends AbstractPropertyMeta<Id> {
    IdPropertyMeta(@Nullable Id annotation) {
        super(annotation);
    }

    @Override
    public PropertyKind<Id> kind() {
        return PropertyKind.ID;
    }

    @Override
    public boolean required() {
        return get(Id::required, Id.DEFAULT_REQUIRED);
    }

    @Override
    public boolean writeNull() {
        return true;
    }

    @Override
    public boolean mutate() {
        return false;
    }
}
