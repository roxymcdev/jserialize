package net.roxymc.jserialize.adapter.optional;

import net.roxymc.jserialize.adapter.TypeAdapter;
import org.jspecify.annotations.Nullable;

import java.util.OptionalLong;

public final class OptionalLongAdapter extends AbstractOptionalAdapter<OptionalLong, Long> {
    public static final TypeAdapter<OptionalLong> INSTANCE = new OptionalLongAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    OptionalLongAdapter() {
        super(OptionalLong.class, Long.class);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected OptionalLong createOptional(@Nullable Long value) {
        return value != null ? OptionalLong.of(value) : OptionalLong.empty();
    }

    @Override
    protected @Nullable Long getOptional(OptionalLong optional) {
        return optional.isPresent() ? optional.getAsLong() : null;
    }
}
