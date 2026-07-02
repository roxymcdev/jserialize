package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

public final class DoubleKeyAdapter extends AbstractKeyAdapter<Double> {
    private static final TypeRef<Double> TYPE = TypeRef.of(Double.class);

    public static final KeyAdapter<Double> INSTANCE = new DoubleKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private DoubleKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Double parse(String value) {
        return Double.parseDouble(value);
    }

    @Override
    public TypeRef<? extends Double> type() {
        return TYPE;
    }
}
