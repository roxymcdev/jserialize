package net.roxymc.jserialize.adapter.gson;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import net.roxymc.jserialize.adapter.ReadContext;
import net.roxymc.jserialize.adapter.ReaderAdapter;
import net.roxymc.jserialize.creator.PropertyValue;
import net.roxymc.jserialize.model.property.PropertyModel;
import net.roxymc.jserialize.util.Pair;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.Iterator;

final class GsonReaderAdapter implements ReaderAdapter<JsonElement> {
    private final ObjectAdapter<?> adapter;
    private final JsonReader reader;

    GsonReaderAdapter(ObjectAdapter<?> adapter, JsonReader reader) {
        this.adapter = adapter;
        this.reader = reader;
    }

    @Override
    public Iterable<Pair<String, @Nullable PropertyModel>> properties() {
        return () -> new Iterator<>() {
            @Override
            public boolean hasNext() {
                try {
                    return reader.peek() != JsonToken.END_OBJECT;
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public Pair<String, @Nullable PropertyModel> next() {
                try {
                    String name = reader.nextName();
                    return new Pair<>(name, adapter.classModel.properties().get(name));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
    }

    @Override
    public void readStart() throws Throwable {
        reader.beginObject();
    }

    @Override
    public PropertyValue<?> readValue(String name, Type type) throws Throwable {
        TypeAdapter<?> adapter = this.adapter.gson.getAdapter(TypeToken.get(type));

        if (adapter instanceof ObjectAdapter<?>) {
            return readValue((ObjectAdapter<?>) adapter);
        }

        return PropertyValue.of(adapter.nullSafe().read(reader));
    }

    private <U> PropertyValue<U> readValue(ObjectAdapter<U> adapter) throws Throwable {
        JsonElement element = this.adapter.gson.getAdapter(ObjectAdapter.RAW_TYPE).read(reader);

        return (parent, instance) -> adapter.engine.read(
                new GsonReaderAdapter(adapter, new JsonTreeReader(element)),
                new ReadContext<>(parent, instance)
        );
    }

    @Override
    public JsonElement readRawValue(String name) throws Throwable {
        return adapter.gson.getAdapter(ObjectAdapter.RAW_TYPE).read(reader);
    }

    @Override
    public void skipValue() throws Throwable {
        reader.skipValue();
    }

    @Override
    public void readEnd() throws Throwable {
        reader.endObject();
    }
}
