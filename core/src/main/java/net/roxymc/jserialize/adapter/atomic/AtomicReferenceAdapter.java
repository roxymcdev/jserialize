package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public final class AtomicReferenceAdapter<V extends @Nullable Object> extends AbstractAtomicAdapter<AtomicReference<V>, V> {
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Factory FACTORY = Factory.exactRaw(AtomicReference.class, type -> new AtomicReferenceAdapter(type));

    private AtomicReferenceAdapter(TypeRef<@Nullable AtomicReference<V>> atomicType) {
        super(atomicType);
    }

    public static Factory factory() {
        return FACTORY;
    }

    @Override
    protected AtomicReference<V> createAtomic() {
        return new AtomicReference<>();
    }

    @Override
    protected V getAtomic(AtomicReference<V> atomic) {
        return atomic.get();
    }

    @Override
    protected void setAtomic(AtomicReference<V> atomic, @Nullable V value) {
        atomic.set(value);
    }
}
