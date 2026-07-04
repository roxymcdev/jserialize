package net.roxymc.jserialize.adapter.optional;

import net.roxymc.jserialize.adapter.TypeAdapter;
import org.jspecify.annotations.Nullable;

import java.util.OptionalInt;

public final class OptionalIntAdapter extends AbstractOptionalAdapter<OptionalInt, Integer> {
    public static final TypeAdapter<OptionalInt> INSTANCE = new OptionalIntAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    OptionalIntAdapter() {
        super(OptionalInt.class, Integer.class);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected OptionalInt createOptional(@Nullable Integer value) {
        return value != null ? OptionalInt.of(value) : OptionalInt.empty();
    }

    @Override
    protected @Nullable Integer getOptional(OptionalInt optional) {
        return optional.isPresent() ? optional.getAsInt() : null;
    }
}
