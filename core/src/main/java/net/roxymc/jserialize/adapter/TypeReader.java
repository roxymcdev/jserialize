package net.roxymc.jserialize.adapter;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

public interface TypeReader<T> {
    @Nullable T read(Reader reader, TypeToken<? extends T> type, ReadContext context) throws IOException;
}
