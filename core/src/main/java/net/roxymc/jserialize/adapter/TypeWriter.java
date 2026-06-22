package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

public interface TypeWriter<T> {
    void write(Writer writer, TypeToken<? extends T> type, @Nullable T value, WriteContext context) throws IOException;
}
