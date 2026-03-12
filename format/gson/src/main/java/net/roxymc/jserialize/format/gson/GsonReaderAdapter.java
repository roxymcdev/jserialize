package net.roxymc.jserialize.format.gson;

import com.google.gson.stream.JsonReader;
import net.roxymc.jserialize.AbstractReader;
import net.roxymc.jserialize.token.TokenType;

import java.io.IOException;

final class GsonReaderAdapter extends AbstractReader {
    final JsonReader reader;

    public GsonReaderAdapter(JsonReader reader) {
        this.reader = reader;
    }

    @Override
    public TokenType peek() throws IOException {
        switch (reader.peek()) {
            case NAME:
                return TokenType.NAME;
            case BEGIN_OBJECT:
                return TokenType.OBJECT_START;
            case END_OBJECT:
                return TokenType.OBJECT_END;
            case BEGIN_ARRAY:
                return TokenType.ARRAY_START;
            case END_ARRAY:
                return TokenType.ARRAY_END;
            case STRING:
                return TokenType.STRING;
            case BOOLEAN:
                return TokenType.BOOLEAN;
            case NUMBER:
                return TokenType.NUMBER;
            case NULL:
                return TokenType.NULL;
            case END_DOCUMENT:
                return TokenType.END;
            default:
                return TokenType.UNKNOWN;
        }
    }

    @Override
    public String readName() throws IOException {
        checkToken(peek(), TokenType.NAME);
        return reader.nextName();
    }

    @Override
    public void readObjectStart() throws IOException {
        checkToken(peek(), TokenType.OBJECT_START);
        reader.beginObject();
    }

    @Override
    public void readObjectEnd() throws IOException {
        checkToken(peek(), TokenType.OBJECT_END);
        reader.endObject();
    }

    @Override
    public void readArrayStart() throws IOException {
        checkToken(peek(), TokenType.ARRAY_START);
        reader.beginArray();
    }

    @Override
    public void readArrayEnd() throws IOException {
        checkToken(peek(), TokenType.ARRAY_END);
        reader.endArray();
    }

    @Override
    public String readString() throws IOException {
        checkToken(peek(), TokenType.STRING);
        return reader.nextString();
    }

    @Override
    public boolean readBoolean() throws IOException {
        checkToken(peek(), TokenType.BOOLEAN);
        return reader.nextBoolean();
    }

    @Override
    public int readInt() throws IOException {
        checkToken(peek(), TokenType.NUMBER);
        return reader.nextInt();
    }

    @Override
    public long readLong() throws IOException {
        checkToken(peek(), TokenType.NUMBER);
        return reader.nextLong();
    }

    @Override
    public double readDouble() throws IOException {
        checkToken(peek(), TokenType.NUMBER);
        return reader.nextDouble();
    }

    @Override
    public byte[] readBinary() {
        throw notSupported(TokenType.BINARY);
    }

    @Override
    public void readNull() throws IOException {
        checkToken(peek(), TokenType.NULL);
        reader.nextNull();
    }

    @Override
    public void skipValue() throws IOException {
        checkToken(peek(), type -> type.kind().isValue());
        reader.skipValue();
    }
}
