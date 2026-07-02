package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.Reader;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

public interface TypeReader<T> {
    @Nullable T read(Reader reader, ReadContext context) throws IOException;
}
