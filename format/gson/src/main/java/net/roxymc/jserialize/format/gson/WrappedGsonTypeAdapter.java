package net.roxymc.jserialize.format.gson;

import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

final class WrappedGsonTypeAdapter<T> implements TypeAdapter<T> {
    private final TypeRef<T> type;
    private final com.google.gson.TypeAdapter<T> adapter;

    WrappedGsonTypeAdapter(TypeRef<T> type, com.google.gson.TypeAdapter<T> adapter) {
        this.type = type;
        this.adapter = adapter;
    }

    @Override
    public T read(Reader reader, ReadContext context) throws IOException {
        return adapter.read(((GsonReaderAdapter) reader).reader);
    }

    @Override
    public void write(Writer writer, @Nullable T value, WriteContext context) throws IOException {
        //noinspection DataFlowIssue - Gson allows null value
        adapter.write(((GsonWriterAdapter) writer).writer, value);
    }

    @Override
    public TypeRef<? extends T> type() {
        return type;
    }
}
