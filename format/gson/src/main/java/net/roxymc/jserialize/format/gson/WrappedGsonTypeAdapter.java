package net.roxymc.jserialize.format.gson;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

final class WrappedGsonTypeAdapter<T> implements TypeAdapter<T> {
    private final com.google.gson.TypeAdapter<T> adapter;

    WrappedGsonTypeAdapter(com.google.gson.TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public T read(Reader reader, TypeToken<? extends T> type, ReadContext context) throws IOException {
        return adapter.read(((GsonReaderAdapter) reader).reader);
    }

    @Override
    public void write(Writer writer, TypeToken<? extends T> type, @Nullable T value, WriteContext context) throws IOException {
        //noinspection DataFlowIssue - Gson allows null value
        adapter.write(((GsonWriterAdapter) writer).writer, value);
    }
}
