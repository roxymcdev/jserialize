package net.roxymc.jserialize.adapter.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.roxymc.jserialize.adapter.*;
import net.roxymc.jserialize.model.ClassModel;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

final class ObjectAdapter<T> extends TypeAdapter<T> {
    static final Class<JsonElement> RAW_TYPE = JsonElement.class;

    final ObjectAdapterEngine<T> engine;
    final ClassModel<T> classModel;
    final Gson gson;

    ObjectAdapter(ClassModel<T> classModel, Gson gson) {
        this.engine = new ObjectAdapterEngine<>(classModel, new GsonUtils(this));
        this.classModel = classModel;
        this.gson = gson;
    }

    @Override
    public @Nullable T read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        try {
            return engine.read(new GsonReaderAdapter(this, reader), ReadContext.empty());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(JsonWriter writer, @Nullable T value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }

        try {
            engine.write(new GsonWriterAdapter(this, writer), value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
