package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.AbstractReader;
import net.roxymc.jserialize.format.TokenTypeInfo;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.TokenTypes;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
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
                    return tokenType = TokenTypes.END;
                }

                return tokenType = (container == Container.OBJECT ? TokenTypes.OBJECT_END : TokenTypes.ARRAY_END);
            }

            if (containerStack.peek() == Container.OBJECT && lastTokenType != TokenTypes.NAME) {
                return tokenType = TokenTypes.NAME;
            }

            return tokenType = BsonUtils.TOKEN_TYPES.fromNative(bsonType);
        } finally {
            lastTokenType = tokenType;
        }
    }

    @Override
    public void read(TokenType.NonValued tokenType) throws IOException {
        checkToken(peek(), tokenType);

        if (tokenType == TokenTypes.OBJECT_START) {
            readObjectStart();
            return;
        } else if (tokenType == TokenTypes.OBJECT_END) {
            readObjectEnd();
            return;
        } else if (tokenType == TokenTypes.ARRAY_START) {
            readArrayStart();
            return;
        } else if (tokenType == TokenTypes.ARRAY_END) {
            readArrayEnd();
            return;
        }

        TokenTypeInfo.NonValued<BsonReader, BsonWriter> info = BsonUtils.TOKEN_TYPES.get(tokenType);
        if (info == null) {
            throw notSupported(tokenType);
        }

        info.read(reader);
        resetToken();
    }

    @Override
    public <T> T read(TokenType.Valued<T> tokenType) throws IOException {
        checkToken(peek(), tokenType);

        TokenTypeInfo.Valued<BsonReader, BsonWriter, T> info = BsonUtils.TOKEN_TYPES.get(tokenType);
        if (info == null) {
            throw notSupported(tokenType);
        }

        T value = info.read(reader);
        resetToken();
        return value;
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
    public void readObjectStart() {
        checkToken(peek(), TokenTypes.OBJECT_START);

        reader.readStartDocument();
        containerStack.push(Container.OBJECT);
        resetToken();
    }

    @Override
    public void readObjectEnd() {
        checkToken(peek(), TokenTypes.OBJECT_END);

        reader.readEndDocument();
        containerStack.pop();
        resetToken();
    }

    @Override
    public void readArrayStart() {
        checkToken(peek(), TokenTypes.ARRAY_START);

        reader.readStartArray();
        containerStack.push(Container.ARRAY);
        resetToken();
    }

    @Override
    public void readArrayEnd() {
        checkToken(peek(), TokenTypes.ARRAY_END);

        reader.readEndArray();
        containerStack.pop();
        resetToken();
    }

    @Override
    public boolean readBoolean() {
        checkToken(peek(), TokenTypes.BOOLEAN);

        boolean value = reader.readBoolean();
        resetToken();
        return value;
    }

    @Override
    public int readInt() {
        checkToken(peek(), TokenTypes.INT);

        int value = reader.readInt32();
        resetToken();
        return value;
    }

    @Override
    public long readLong() {
        checkToken(peek(), TokenTypes.LONG);

        long value = reader.readInt64();
        resetToken();
        return value;
    }

    @Override
    public double readDouble() {
        checkToken(peek(), TokenTypes.DOUBLE);

        double value = reader.readDouble();
        resetToken();
        return value;
    }

    @Override
    public void skipValue() {
        checkToken(peek(), type -> type.kind().marksValue());

        reader.skipValue();
        resetToken();
    }

    private enum Container {
        OBJECT,
        ARRAY
    }
}
