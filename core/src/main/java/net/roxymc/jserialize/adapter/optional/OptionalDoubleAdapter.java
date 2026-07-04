package net.roxymc.jserialize.adapter.optional;

import net.roxymc.jserialize.adapter.TypeAdapter;
import org.jspecify.annotations.Nullable;

import java.util.OptionalDouble;

public final class OptionalDoubleAdapter extends AbstractOptionalAdapter<OptionalDouble, Double> {
    public static final TypeAdapter<OptionalDouble> INSTANCE = new OptionalDoubleAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    OptionalDoubleAdapter() {
        super(OptionalDouble.class, Double.class);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected OptionalDouble createOptional(@Nullable Double value) {
        return value != null ? OptionalDouble.of(value) : OptionalDouble.empty();
    }

    @Override
    protected @Nullable Double getOptional(OptionalDouble optional) {
        return optional.isPresent() ? optional.getAsDouble() : null;
    }
}
