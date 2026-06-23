package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class EnumKeyAdapter implements KeyAdapter<Enum<?>> {
    private static final KeyAdapter.Factory FACTORY = new KeyAdapter.Factory() {
        @Override
        public <T> @Nullable KeyAdapter<T> create(TypeToken<T> type, TypeAdapters adapters) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) type.getRawType();
            if (!enumType.isEnum()) {
                return null;
            }

            @SuppressWarnings("unchecked")
            KeyAdapter<T> adapter = (KeyAdapter<T>) new EnumKeyAdapter(enumType);
            return adapter;
        }
    };

    private final Class<? extends Enum<?>> enumType;

    private EnumKeyAdapter(Class<? extends Enum<?>> enumType) {
        this.enumType = enumType;
    }

    public static KeyAdapter.Factory factory() {
        return FACTORY;
    }

    @Override
    public @Nullable Enum<?> decode(@Nullable String value) {
        if (value == null) {
            return null;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        Enum<?> resolved = Enum.valueOf((Class) enumType, value);
        return resolved;
    }

    @Override
    public String encode(@Nullable Enum<?> value) {
        return nonNull(value, "value").name();
    }
}
