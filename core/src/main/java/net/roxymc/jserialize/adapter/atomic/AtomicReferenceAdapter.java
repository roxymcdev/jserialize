package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public final class AtomicReferenceAdapter<V> extends AbstractAtomicAdapter<AtomicReference<@Nullable V>, V> {
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Factory FACTORY = Factory.exactRaw(AtomicReference.class, type -> new AtomicReferenceAdapter(type));

    private AtomicReferenceAdapter(TypeRef<AtomicReference<@Nullable V>> atomicType) {
        super(atomicType);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected AtomicReference<@Nullable V> createAtomic() {
        return new AtomicReference<>();
    }

    @Override
    protected @Nullable V getAtomic(AtomicReference<@Nullable V> atomic) {
        return atomic.get();
    }

    @Override
    protected void setAtomic(AtomicReference<@Nullable V> atomic, @Nullable V value) {
        atomic.set(value);
    }
}
