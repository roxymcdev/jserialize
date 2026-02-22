package net.roxymc.jserialize.adapter.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import net.roxymc.jserialize.adapter.WriterAdapter;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;

final class GsonWriterAdapter implements WriterAdapter {
    private final ObjectAdapter<?> adapter;
    private final JsonWriter writer;

    GsonWriterAdapter(ObjectAdapter<?> adapter, JsonWriter writer) {
        this.adapter = adapter;
        this.writer = writer;
    }

    @Override
    public void writeStart() throws Throwable {
        writer.beginObject();
    }

    @Override
    public void writeProperty(String name, Type type, @Nullable Object value) throws Throwable {
        @SuppressWarnings("unchecked")
        TypeAdapter<Object> adapter = (TypeAdapter<Object>) this.adapter.gson.getAdapter(TypeToken.get(type));

        writer.name(name);
        //noinspection DataFlowIssue
        adapter.nullSafe().write(writer, value);
    }

    @Override
    public void writeEnd() throws Throwable {
        writer.endObject();
    }
}
