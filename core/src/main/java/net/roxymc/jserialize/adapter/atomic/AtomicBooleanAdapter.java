package net.roxymc.jserialize.adapter.atomic;

import org.jspecify.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public final class AtomicBooleanAdapter extends AbstractAtomicAdapter<AtomicBoolean, Boolean> {
    public static final AtomicBooleanAdapter INSTANCE = new AtomicBooleanAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private AtomicBooleanAdapter() {
        super(AtomicBoolean.class, boolean.class);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected AtomicBoolean createAtomic() {
        return new AtomicBoolean();
    }

    @Override
    protected Boolean getAtomic(AtomicBoolean atomic) {
        return atomic.get();
    }

    @Override
    protected void setAtomic(AtomicBoolean atomic, @Nullable Boolean value) {
        if (value == null) {
            throw new IllegalStateException("value cannot be null for AtomicBoolean");
        }

        atomic.set(value);
    }
}
