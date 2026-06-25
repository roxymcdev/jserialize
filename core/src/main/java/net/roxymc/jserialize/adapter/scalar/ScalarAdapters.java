package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.TypeAdapter;

import static net.roxymc.jserialize.adapter.TypeAdapter.Factory.exactBoxed;
import static net.roxymc.jserialize.adapter.TypeAdapter.Factory.exactRaw;

public final class ScalarAdapters {
    public static final TypeAdapter<Character> CHARACTER = new CharacterAdapter();
    public static final TypeAdapter<Boolean> BOOLEAN = new BooleanAdapter();
    public static final TypeAdapter<Byte> BYTE = new ByteAdapter();
    public static final TypeAdapter<Double> DOUBLE = new DoubleAdapter();
    public static final TypeAdapter<Float> FLOAT = new FloatAdapter();
    public static final TypeAdapter<Integer> INTEGER = new IntegerAdapter();
    public static final TypeAdapter<Long> LONG = new LongAdapter();
    public static final TypeAdapter<Short> SHORT = new ShortAdapter();
    public static final TypeAdapter<Number> NUMBER = new NumberAdapter();
    public static final TypeAdapter<String> STRING = new StringAdapter();
    public static final TypeAdapter<java.util.UUID> UUID = new UUIDAdapter();

    private static final TypeAdapter.Factory FACTORY = TypeAdapter.Factory.composite(
            exactBoxed(Character.class, CHARACTER),
            exactBoxed(Boolean.class, BOOLEAN),
            exactBoxed(Byte.class, BYTE),
            exactBoxed(Double.class, DOUBLE),
            exactBoxed(Float.class, FLOAT),
            exactBoxed(Integer.class, INTEGER),
            exactBoxed(Long.class, LONG),
            exactBoxed(Short.class, SHORT),
            exactRaw(Number.class, NUMBER),
            exactRaw(String.class, STRING),
            exactRaw(java.util.UUID.class, UUID)
    );

    private ScalarAdapters() {
    }

    public static TypeAdapter.Factory factory() {
        return FACTORY;
    }
}
