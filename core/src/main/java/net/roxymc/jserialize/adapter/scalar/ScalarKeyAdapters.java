package net.roxymc.jserialize.adapter.scalar;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ScalarKeyAdapters {
    private static final Map<Class<?>, KeyAdapter<?>> ADAPTERS = new HashMap<>();

    public static final KeyAdapter<Character> CHAR = keyAdapter(Character.class, value -> {
        if (value.length() != 1) {
            throw new IllegalStateException("value must be a single character");
        }

        return value.charAt(0);
    });
    public static final KeyAdapter<Boolean> BOOLEAN = keyAdapter(Boolean.class, Boolean::parseBoolean);
    public static final KeyAdapter<Byte> BYTE = keyAdapter(Byte.class, Byte::parseByte);
    public static final KeyAdapter<Double> DOUBLE = keyAdapter(Double.class, Double::parseDouble);
    public static final KeyAdapter<Float> FLOAT = keyAdapter(Float.class, Float::parseFloat);
    public static final KeyAdapter<Integer> INTEGER = keyAdapter(Integer.class, Integer::parseInt);
    public static final KeyAdapter<Long> LONG = keyAdapter(Long.class, Long::parseLong);
    public static final KeyAdapter<Short> SHORT = keyAdapter(Short.class, Short::parseShort);
    public static final KeyAdapter<String> STRING = keyAdapter(String.class, Function.identity());

    private static final KeyAdapter.Factory FACTORY = new KeyAdapter.Factory() {
        @Override
        public @Nullable <T> KeyAdapter<T> create(TypeToken<T> type, TypeAdapters adapters) {
            Type boxedType = GenericTypeReflector.box(type.getRawType());

            @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
            KeyAdapter<T> keyAdapter = (KeyAdapter<T>) ADAPTERS.get(boxedType);
            return keyAdapter;
        }
    };

    private ScalarKeyAdapters() {
    }

    public static KeyAdapter.Factory factory() {
        return FACTORY;
    }

    private static <T> KeyAdapter<T> keyAdapter(Class<T> type, Function<String, T> function) {
        @SuppressWarnings("unchecked")
        KeyAdapter<T> keyAdapter = (KeyAdapter<T>) ADAPTERS.compute(type, ($, adapter) -> {
            if (adapter != null) {
                throw new IllegalStateException("key adapter for " + type + " is already registered");
            }

            return KeyAdapter.of(value -> value != null ? function.apply(value) : null, String::valueOf);
        });
        return keyAdapter;
    }
}
