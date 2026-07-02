package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;

public final class FloatKeyAdapter extends AbstractKeyAdapter<Float> {
    private static final TypeRef<Float> TYPE = TypeRef.of(Float.class);

    public static final KeyAdapter<Float> INSTANCE = new FloatKeyAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private FloatKeyAdapter() {
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected Float parse(String value) {
        return Float.parseFloat(value);
    }

    @Override
    public TypeRef<? extends Float> type() {
        return TYPE;
    }
}
