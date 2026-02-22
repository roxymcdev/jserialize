package net.roxymc.jserialize.adapter;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;

public interface WriterAdapter {
    default void writeStart() throws Throwable {
    }

    void writeProperty(String name, Type type, @Nullable Object value) throws Throwable;

    default void writeEnd() throws Throwable {
    }
}
