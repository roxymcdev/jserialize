package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.Writer;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

public interface TypeWriter<T> {
    void write(Writer writer, @Nullable T value, WriteContext context) throws IOException;
}
