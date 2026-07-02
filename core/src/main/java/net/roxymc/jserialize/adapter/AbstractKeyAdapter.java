package net.roxymc.jserialize.adapter;

import org.jspecify.annotations.Nullable;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public abstract class AbstractKeyAdapter<T> implements KeyAdapter<T> {
    protected AbstractKeyAdapter() {
    }

    @Override
    public final @Nullable T decode(@Nullable String value) {
        return value != null ? parse(value) : null;
    }

    protected abstract T parse(String value);

    @Override
    public final String encode(@Nullable T value) {
        return nonNull(value, "value").toString();
    }
}
