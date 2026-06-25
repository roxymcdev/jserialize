package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeRef;
import net.roxymc.jserialize.util.TypeUtils;
import org.jspecify.annotations.Nullable;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class EnumKeyAdapter implements KeyAdapter<Enum<?>> {
    private static final KeyAdapter.Factory FACTORY = new KeyAdapter.Factory() {
        @Override
        public <T> @Nullable KeyAdapter<T> createKey(TypeRef<T> type, TypeAdapters adapters) {
            Class<? super T> enumType = type.getRawType();
            if (!TypeUtils.isEnum(enumType)) {
                return null;
            }

            @SuppressWarnings("unchecked")
            KeyAdapter<T> adapter = (KeyAdapter<T>) new EnumKeyAdapter(enumType);
            return adapter;
        }
    };

    private final Class<?> enumType;

    private EnumKeyAdapter(Class<?> enumType) {
        this.enumType = enumType;
    }

    public static KeyAdapter.Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable Enum<?> decode(@Nullable String value) {
        return value != null ? Enum.valueOf(enumType.asSubclass(Enum.class), value) : null;
    }

    @Override
    public String encode(@Nullable Enum<?> value) {
        return nonNull(value, "value").name();
    }
}
