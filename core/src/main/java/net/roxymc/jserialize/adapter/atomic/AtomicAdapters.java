package net.roxymc.jserialize.adapter.atomic;

import net.roxymc.jserialize.adapter.TypeAdapter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static net.roxymc.jserialize.adapter.TypeAdapter.Factory.polymorphic;

public final class AtomicAdapters {
    public static final TypeAdapter<AtomicBoolean> BOOLEAN = new AtomicBooleanAdapter();
    public static final TypeAdapter<AtomicLong> LONG = new AtomicLongAdapter();
    public static final TypeAdapter<AtomicInteger> INTEGER = new AtomicIntegerAdapter();
    public static final TypeAdapter<AtomicReference<?>> REFERENCE = new AtomicReferenceAdapter();

    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.composite(
            polymorphic(AtomicBoolean.class, BOOLEAN),
            polymorphic(AtomicLong.class, LONG),
            polymorphic(AtomicInteger.class, INTEGER),
            polymorphic(AtomicReference.class, REFERENCE)
    );

    private AtomicAdapters() {
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }
}
