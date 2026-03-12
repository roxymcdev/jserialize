package net.roxymc.jserialize.format.gson;

import com.google.gson.stream.JsonWriter;
import net.roxymc.jserialize.AbstractWriter;
import net.roxymc.jserialize.token.TokenType;

import java.io.IOException;

final class GsonWriterAdapter extends AbstractWriter {
    final JsonWriter writer;

    GsonWriterAdapter(JsonWriter writer) {
        this.writer = writer;
    }

    @Override
    public void writeName(String name) throws IOException {
        writer.name(name);
    }

    @Override
    public void writeObjectStart() throws IOException {
        writer.beginObject();
    }

    @Override
    public void writeObjectEnd() throws IOException {
        writer.endObject();
    }

    @Override
    public void writeArrayStart() throws IOException {
        writer.beginArray();
    }

    @Override
    public void writeArrayEnd() throws IOException {
        writer.endArray();
    }

    @Override
    public void writeString(String value) throws IOException {
        writer.value(value);
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        writer.value(value);
    }

    @Override
    public void writeInt(int value) throws IOException {
        writer.value(value);
    }

    @Override
    public void writeLong(long value) throws IOException {
        writer.value(value);
    }

    @Override
    public void writeDouble(double value) throws IOException {
        writer.value(value);
    }

    @Override
    public void writeBinary(byte[] value) {
        throw notSupported(TokenType.BINARY);
    }

    @Override
    public void writeNull() throws IOException {
        writer.nullValue();
    }
}
