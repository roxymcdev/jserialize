package net.roxymc.jserialize.adapter.optional;

import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public final class OptionalAdapter<V> extends AbstractOptionalAdapter<Optional<V>, V> {
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Factory FACTORY = Factory.exactRaw(Optional.class, type -> new OptionalAdapter(type));

    private OptionalAdapter(TypeRef<Optional<V>> optionalType) {
        super(optionalType);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Optional<V> createOptional(@Nullable V value) {
        return Optional.ofNullable(value);
    }

    @Override
    protected @Nullable V getOptional(Optional<V> optional) {
        return optional.orElse(null);
    }
}
