package net.roxymc.jserialize.format.gson;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.roxymc.jserialize.Reader;
import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.TypeAdapter;
import net.roxymc.jserialize.adapter.TypeAdapters;
import net.roxymc.jserialize.adapter.WriteContext;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

final class WrappedTypeAdapter<T> extends com.google.gson.TypeAdapter<T> {
    private final TypeRef<? extends T> type;
    final TypeAdapter<T> typeAdapter;

    private final ReadContext readCtx;
    private final WriteContext writeCtx;

    WrappedTypeAdapter(TypeRef<? extends T> type, TypeAdapter<T> typeAdapter, TypeAdapters typeAdapters) {
        this.type = type;
        this.typeAdapter = typeAdapter;

        this.readCtx = ReadContext.of(typeAdapters, GsonUtils.INSTANCE);
        this.writeCtx = WriteContext.of(typeAdapters, GsonUtils.INSTANCE);
    }

    @Override
    public @Nullable T read(JsonReader jsonReader) throws IOException {
        Reader reader = new GsonReaderAdapter(jsonReader);

        return typeAdapter.read(reader, type, readCtx);
    }

    @Override
    public void write(JsonWriter jsonWriter, @Nullable T value) throws IOException {
        Writer writer = new GsonWriterAdapter(jsonWriter);

        typeAdapter.write(writer, type, value, writeCtx);
    }
}
