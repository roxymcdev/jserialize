package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.AbstractReader;
import net.roxymc.jserialize.format.TokenTypeInfo;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.TokenTypes;
import org.bson.AbstractBsonReader;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;

import java.io.IOException;
import java.util.Objects;

final class StandardBsonReaderAdapter extends AbstractReader implements BsonReaderAdapter {
    private final AbstractBsonReader reader;

    StandardBsonReaderAdapter(AbstractBsonReader reader) {
        this.reader = reader;
    }

    @Override
    public BsonReader getBsonReader() {
        return reader;
    }

    @Override
    public TokenType peek() {
        if (reader.getState() == AbstractBsonReader.State.TYPE) {
            reader.readBsonType();
        }

        if (reader.getState() == AbstractBsonReader.State.NAME) {
            return TokenTypes.NAME;
        }

        // TODO actually, that's not necessarily true. After State.DONE, reader usually comes back to State.VALUE with BsonType.DOCUMENT
        // I'm not yet sure how to handle that - BsonReader doesn't really support the concept of END
        // (except for throwing an exception when trying to read more data)
        /*
        if (reader.getState() == AbstractBsonReader.STATE.DONE) {
            return Token.END;
        }
         */

        BsonType bsonType = Objects.requireNonNullElseGet(reader.getCurrentBsonType(), reader::readBsonType);

        if (reader.getState() == AbstractBsonReader.State.END_OF_DOCUMENT) {
            return TokenTypes.OBJECT_END;
        } else if (reader.getState() == AbstractBsonReader.State.END_OF_ARRAY) {
            return TokenTypes.ARRAY_END;
        }

        return BsonUtils.TOKEN_TYPES.fromNative(bsonType);
    }

    @Override
    public void read(TokenType.NonValued tokenType) throws IOException {
        checkToken(peek(), tokenType);

        TokenTypeInfo.NonValued<BsonReader, BsonWriter> info = BsonUtils.TOKEN_TYPES.get(tokenType);
        if (info == null) {
            throw notSupported(tokenType);
        }

        info.read(reader);
    }

    @Override
    public <T> T read(TokenType.Valued<T> tokenType) throws IOException {
        checkToken(peek(), tokenType);

        TokenTypeInfo.Valued<BsonReader, BsonWriter, T> info = BsonUtils.TOKEN_TYPES.get(tokenType);
        if (info == null) {
            throw notSupported(tokenType);
        }

        return info.read(reader);
    }

    @Override
    public boolean readBoolean() {
        checkToken(peek(), TokenTypes.BOOLEAN);
        return reader.readBoolean();
    }

    @Override
    public int readInt() {
        checkToken(peek(), TokenTypes.INT);
        return reader.readInt32();
    }

    @Override
    public long readLong() {
        checkToken(peek(), TokenTypes.LONG);
        return reader.readInt64();
    }

    @Override
    public double readDouble() {
        checkToken(peek(), TokenTypes.DOUBLE);
        return reader.readDouble();
    }

    @Override
    public void skipValue() {
        checkToken(peek(), type -> type.kind().hasValue());
        reader.skipValue();
    }
}
