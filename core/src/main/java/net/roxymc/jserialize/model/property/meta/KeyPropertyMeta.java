package net.roxymc.jserialize.model.property.meta;

import net.roxymc.jserialize.annotation.Key;
import org.jspecify.annotations.Nullable;

final class KeyPropertyMeta extends AbstractPropertyMeta<Key> {
    KeyPropertyMeta(@Nullable Key annotation) {
        super(annotation);
    }

    @Override
    public PropertyKind<Key> kind() {
        return PropertyKind.KEY;
    }

    @Override
    public boolean required() {
        return get(Key::required, true);
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
