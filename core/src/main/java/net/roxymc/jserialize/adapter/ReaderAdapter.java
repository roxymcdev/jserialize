package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.creator.PropertyValue;

import java.lang.reflect.Type;

public interface ReaderAdapter<R> {
    Iterable<String> propertyNames();

    default void readStart() throws Throwable {
    }

    PropertyValue<?> readValue(String name, Type type) throws Throwable;

    R readRawValue(String name) throws Throwable;

    default void skipValue() throws Throwable {
    }

    default void readEnd() throws Throwable {
    }
}
