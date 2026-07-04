package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.adapter.TypeAdapter;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.atomic.AtomicLong;

public final class AtomicLongAdapter extends AbstractAtomicAdapter<AtomicLong, Long> {
    public static final TypeAdapter<AtomicLong> INSTANCE = new AtomicLongAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private AtomicLongAdapter() {
        super(AtomicLong.class, long.class);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected AtomicLong createAtomic() {
        return new AtomicLong();
    }

    @Override
    protected Long getAtomic(AtomicLong atomic) {
        return atomic.get();
    }

    @Override
    protected void setAtomic(AtomicLong atomic, @Nullable Long value) {
        if (value == null) {
            throw new IllegalStateException("value cannot be null for AtomicLong");
        }

        atomic.set(value);
    }
}
