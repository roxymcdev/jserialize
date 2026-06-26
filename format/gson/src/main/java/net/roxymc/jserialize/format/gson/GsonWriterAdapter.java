package net.roxymc.jserialize.format.gson;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.roxymc.jserialize.AbstractWriter;
import net.roxymc.jserialize.format.TokenTypeInfo;
import net.roxymc.jserialize.token.TokenType;

import java.io.IOException;

final class GsonWriterAdapter extends AbstractWriter {
    final JsonWriter writer;

    GsonWriterAdapter(JsonWriter writer) {
        this.writer = writer;
    }

    @Override
    public void write(TokenType.NonValued tokenType) throws IOException {
        TokenTypeInfo.NonValued<JsonReader, JsonWriter> info = GsonUtils.TOKEN_TYPES.get(tokenType);
        if (info == null) {
            throw notSupported(tokenType);
        }

        info.write(writer);
    }

    @Override
    public <T> void write(TokenType.Valued<T> tokenType, T value) throws IOException {
        TokenTypeInfo.Valued<JsonReader, JsonWriter, T> info = GsonUtils.TOKEN_TYPES.get(tokenType);
        if (info == null) {
            throw notSupported(tokenType);
        }

        info.write(writer, value);
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
}
