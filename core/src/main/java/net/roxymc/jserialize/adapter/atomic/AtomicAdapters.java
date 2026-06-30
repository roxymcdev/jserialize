package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.adapter.TypeAdapter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static net.roxymc.jserialize.adapter.TypeAdapter.Factory.exactRaw;

public final class AtomicAdapters {
    public static final TypeAdapter<AtomicBoolean> BOOLEAN = new AtomicPrimitiveAdapter<>(
            AtomicBoolean.class, boolean.class, AtomicBoolean::new, AtomicBoolean::get, AtomicBoolean::set
    );
    public static final TypeAdapter<AtomicLong> LONG = new AtomicPrimitiveAdapter<>(
            AtomicLong.class, long.class, AtomicLong::new, AtomicLong::get, AtomicLong::set
    );
    public static final TypeAdapter<AtomicInteger> INTEGER = new AtomicPrimitiveAdapter<>(
            AtomicInteger.class, int.class, AtomicInteger::new, AtomicInteger::get, AtomicInteger::set
    );
    public static final TypeAdapter<AtomicReference<?>> REFERENCE = new AtomicReferenceAdapter();

    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.composite(
            exactRaw(AtomicBoolean.class, BOOLEAN),
            exactRaw(AtomicLong.class, LONG),
            exactRaw(AtomicInteger.class, INTEGER),
            exactRaw(AtomicReference.class, REFERENCE)
    );

    private AtomicAdapters() {
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }
}
