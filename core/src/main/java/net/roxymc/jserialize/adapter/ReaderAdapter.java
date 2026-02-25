package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.creator.PropertyValue;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.util.Pair;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;

public interface ReaderAdapter<R> {
    Iterable<Pair<String, @Nullable PropertyModel>> properties();

    default void readStart() throws Throwable {
    }

    PropertyValue<?> readValue(String name, Type type) throws Throwable;

    R readRawValue(String name) throws Throwable;

    default void skipValue() throws Throwable {
    }

    default void readEnd() throws Throwable {
    }
}
