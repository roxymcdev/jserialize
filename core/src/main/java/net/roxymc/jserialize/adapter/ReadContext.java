package net.roxymc.jserialize.adapter;

import org.jspecify.annotations.Nullable;

public final class ReadContext<T> {
    private static final ReadContext<?> EMPTY = new ReadContext<>(null, null);

    final @Nullable Object parent;
    final @Nullable T instance;

    public ReadContext(@Nullable Object parent, @Nullable T instance) {
        this.parent = parent;
        this.instance = instance;
    }

    public static <T> ReadContext<T> empty() {
        //noinspection unchecked
        return (ReadContext<T>) EMPTY;
    }
}
