package net.roxymc.jserialize.model.property.meta;

import net.roxymc.jserialize.annotation.JSerial.Parent;
import org.jspecify.annotations.Nullable;

final class ParentPropertyMeta extends AbstractPropertyMeta<Parent> {
    ParentPropertyMeta(@Nullable Parent annotation) {
        super(annotation);
    }

    @Override
    public PropertyKind<Parent> kind() {
        return PropertyKind.PARENT;
    }

    @Override
    public boolean required() {
        return get(Parent::required, Parent.DEFAULT_REQUIRED);
    }

    @Override
    public boolean writeNull() {
        return false;
    }

    @Override
    public boolean mutate() {
        return false;
    }
}
