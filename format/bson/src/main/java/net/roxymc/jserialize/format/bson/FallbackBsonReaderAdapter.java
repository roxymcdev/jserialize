package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.AbstractReader;
import net.roxymc.jserialize.token.TokenType;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

import static java.util.Objects.requireNonNullElseGet;

/**
 * Fallback Bson reader adapter with primitive reader logic.
 * Used only when the BsonReader isn't an AbstractBsonReader (which exposes more state info).
 */
final class FallbackBsonReaderAdapter extends AbstractReader implements BsonReaderAdapter {
    private final BsonReader reader;

    private final Deque<Container> containerStack = new ArrayDeque<>();
    private @Nullable TokenType tokenType, lastTokenType;

    FallbackBsonReaderAdapter(BsonReader reader) {
        this.reader = reader;
    }

    @Override
    public BsonReader getBsonReader() {
        return reader;
    }

    @Override
    public TokenType peek() {
        if (tokenType != null) {
            return tokenType;
        }

        try {
            BsonType bsonType = requireNonNullElseGet(reader.getCurrentBsonType(), reader::readBsonType);

            if (bsonType == BsonType.END_OF_DOCUMENT) {
                Container container = containerStack.peek();
                if (container == null) {
                    // that's the best way we can handle this case,
                    // unfortunately BsonReader interface doesn't expose much info
                    return tokenType = TokenType.END;
                }

                return tokenType = (container == Container.OBJECT ? TokenType.OBJECT_END : TokenType.ARRAY_END);
            }

            if (containerStack.peek() == Container.OBJECT && lastTokenType != TokenType.NAME) {
                return tokenType = TokenType.NAME;
            }

            switch (bsonType) {
                case DOCUMENT:
                    return tokenType = TokenType.OBJECT_START;
                case ARRAY:
                    return tokenType = TokenType.ARRAY_START;
                case BOOLEAN:
                    return tokenType = TokenType.BOOLEAN;
                case STRING:
                    return tokenType = TokenType.STRING;
                case INT32:
                    return tokenType = TokenType.INT;
                case INT64:
                    return tokenType = TokenType.LONG;
                case DOUBLE:
                    return tokenType = TokenType.DOUBLE;
                case BINARY:
                    return tokenType = TokenType.BINARY;
                case NULL:
                    return tokenType = TokenType.NULL;
                default:
                    return tokenType = TokenType.UNKNOWN;
            }
        } finally {
            lastTokenType = tokenType;
        }
    }

    void resetToken() {
        tokenType = null;

        try {
            // BsonReader interface doesn't expose much info about current position,
            // so that was ours best shot at updating current bson type
            reader.readBsonType();
        } catch (Exception ignored) {
        }
    }

    @Override
    public String readName() {
        checkToken(peek(), TokenType.NAME);

        String value = reader.readName();
        resetToken();
        return value;
    }

    @Override
    public void readObjectStart() {
        checkToken(peek(), TokenType.OBJECT_START);

        reader.readStartDocument();
        containerStack.push(Container.OBJECT);
        resetToken();
    }

    @Override
    public void readObjectEnd() {
        checkToken(peek(), TokenType.OBJECT_END);

        reader.readEndDocument();
        containerStack.pop();
        resetToken();
    }

    @Override
    public void readArrayStart() {
        checkToken(peek(), TokenType.ARRAY_START);

        reader.readStartArray();
        containerStack.push(Container.ARRAY);
        resetToken();
    }

    @Override
    public void readArrayEnd() {
        checkToken(peek(), TokenType.ARRAY_END);

        reader.readEndArray();
        containerStack.pop();
        resetToken();
    }

    @Override
    public String readString() {
        checkToken(peek(), TokenType.STRING);

        String value = reader.readString();
        resetToken();
        return value;
    }

    @Override
    public boolean readBoolean() {
        checkToken(peek(), TokenType.BOOLEAN);

        boolean value = reader.readBoolean();
        resetToken();
        return value;
    }

    @Override
    public int readInt() {
        checkToken(peek(), TokenType.INT);

        int value = reader.readInt32();
        resetToken();
        return value;
    }

    @Override
    public long readLong() {
        checkToken(peek(), TokenType.LONG);

        long value = reader.readInt64();
        resetToken();
        return value;
    }

    @Override
    public double readDouble() {
        checkToken(peek(), TokenType.DOUBLE);

        double value = reader.readDouble();
        resetToken();
        return value;
    }

    @Override
    public byte[] readBinary() {
        checkToken(peek(), TokenType.BINARY);

        byte[] value = reader.readBinaryData().getData();
        resetToken();
        return value;
    }

    @Override
    public void readNull() {
        checkToken(peek(), TokenType.NULL);

        reader.readNull();
        resetToken();
    }

    @Override
    public void skipValue() {
        checkToken(peek(), type -> type.kind().isValue());

        reader.skipValue();
        resetToken();
    }

    private enum Container {
        OBJECT,
        ARRAY
    }
}
