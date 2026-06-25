package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.KeyAdapter;

import java.util.function.Function;

import static net.roxymc.jserialize.adapter.KeyAdapter.Factory.exactBoxed;
import static net.roxymc.jserialize.adapter.KeyAdapter.Factory.exactRaw;
import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class ScalarKeyAdapters {
    public static final KeyAdapter<Character> CHARACTER = keyAdapter(value -> {
        if (value.length() != 1) {
            throw new IllegalStateException("value must be a single character");
        }

        return value.charAt(0);
    });
    public static final KeyAdapter<Boolean> BOOLEAN = keyAdapter(Boolean::parseBoolean);
    public static final KeyAdapter<Byte> BYTE = keyAdapter(Byte::parseByte);
    public static final KeyAdapter<Double> DOUBLE = keyAdapter(Double::parseDouble);
    public static final KeyAdapter<Float> FLOAT = keyAdapter(Float::parseFloat);
    public static final KeyAdapter<Integer> INTEGER = keyAdapter(Integer::parseInt);
    public static final KeyAdapter<Long> LONG = keyAdapter(Long::parseLong);
    public static final KeyAdapter<Short> SHORT = keyAdapter(Short::parseShort);
    public static final KeyAdapter<Number> NUMBER = keyAdapter(NumberAdapter::parseNumber);
    public static final KeyAdapter<String> STRING = keyAdapter(Function.identity());
    public static final KeyAdapter<java.util.UUID> UUID = keyAdapter(java.util.UUID::fromString);

    private static final KeyAdapter.Factory FACTORY = KeyAdapter.Factory.composite(
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

    private ScalarKeyAdapters() {
    }

    public static KeyAdapter.Factory factory() {
        return FACTORY;
    }

    private static <T> KeyAdapter<T> keyAdapter(Function<String, T> function) {
        return KeyAdapter.of(value -> value != null ? function.apply(value) : null, value -> nonNull(value, "value").toString());
    }
}
