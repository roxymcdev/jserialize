package net.roxymc.jserialize.format.gson;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.roxymc.jserialize.AbstractReader;
import net.roxymc.jserialize.format.TokenTypeInfo;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.TokenTypes;

import java.io.IOException;

final class GsonReaderAdapter extends AbstractReader {
    final JsonReader reader;

    public GsonReaderAdapter(JsonReader reader) {
        this.reader = reader;
    }

    @Override
    public TokenType peek() throws IOException {
        JsonToken jsonToken = reader.peek();
        if (jsonToken == JsonToken.END_DOCUMENT) {
            return TokenTypes.END;
        }

        return GsonUtils.TOKEN_TYPES.fromNative(jsonToken);
    }

    @Override
    public void read(TokenType.NonValued tokenType) throws IOException {
        checkToken(peek(), tokenType);

        TokenTypeInfo.NonValued<JsonReader, JsonWriter> info = GsonUtils.TOKEN_TYPES.get(tokenType);
        if (info == null) {
            throw notSupported(tokenType);
        }

        info.read(reader);
    }

    @Override
    public <T> T read(TokenType.Valued<T> tokenType) throws IOException {
        if (tokenType == TokenTypes.INT || tokenType == TokenTypes.LONG || tokenType == TokenTypes.DOUBLE) {
            checkToken(peek(), TokenTypes.NUMERIC);
        } else if (tokenType == TokenTypes.STRING) {
            checkToken(peek(), type -> type == TokenTypes.STRING || type == TokenTypes.NUMERIC);
        } else {
            checkToken(peek(), tokenType);
        }

        TokenTypeInfo.Valued<JsonReader, JsonWriter, T> info = GsonUtils.TOKEN_TYPES.get(tokenType);
        if (info == null) {
            throw notSupported(tokenType);
        }

        return info.read(reader);
    }

    @Override
    public boolean readBoolean() throws IOException {
        checkToken(peek(), TokenTypes.BOOLEAN);
        return reader.nextBoolean();
    }

    @Override
    public int readInt() throws IOException {
        checkToken(peek(), TokenTypes.NUMERIC);
        return reader.nextInt();
    }

    @Override
    public long readLong() throws IOException {
        checkToken(peek(), TokenTypes.NUMERIC);
        return reader.nextLong();
    }

    @Override
    public double readDouble() throws IOException {
        checkToken(peek(), TokenTypes.NUMERIC);
        return reader.nextDouble();
    }

    @Override
    public void skipValue() throws IOException {
        checkToken(peek(), type -> type.kind().marksValue());
        reader.skipValue();
    }
}
