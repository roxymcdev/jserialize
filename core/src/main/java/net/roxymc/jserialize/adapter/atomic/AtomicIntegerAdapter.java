package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.adapter.TypeAdapter;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicIntegerAdapter extends AbstractAtomicAdapter<AtomicInteger, Integer> {
    public static final TypeAdapter<AtomicInteger> INSTANCE = new AtomicIntegerAdapter();
    private static final Factory FACTORY = Factory.exact(INSTANCE);

    private AtomicIntegerAdapter() {
        super(AtomicInteger.class, int.class);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected AtomicInteger createAtomic() {
        return new AtomicInteger();
    }

    @Override
    protected Integer getAtomic(AtomicInteger atomic) {
        return atomic.get();
    }

    @Override
    protected void setAtomic(AtomicInteger atomic, @Nullable Integer value) {
        if (value == null) {
            throw new IllegalStateException("value cannot be null for AtomicInteger");
        }

        atomic.set(value);
    }
}
