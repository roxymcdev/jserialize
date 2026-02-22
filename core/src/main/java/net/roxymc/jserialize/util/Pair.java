package net.roxymc.jserialize.util;

import org.jspecify.annotations.Nullable;

public final class Pair<F extends @Nullable Object, S extends @Nullable Object> {
    private final F first;
    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F first() {
        return first;
    }

    public S second() {
        return second;
    }
}
