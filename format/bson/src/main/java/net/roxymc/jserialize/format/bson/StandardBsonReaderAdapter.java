package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.AbstractReader;
import net.roxymc.jserialize.token.TokenType;
import org.bson.AbstractBsonReader;
import org.bson.BsonReader;
import org.bson.BsonType;

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
            return TokenType.NAME;
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
            return TokenType.OBJECT_END;
        } else if (reader.getState() == AbstractBsonReader.State.END_OF_ARRAY) {
            return TokenType.ARRAY_END;
        }

        switch (bsonType) {
            case DOCUMENT:
                return TokenType.OBJECT_START;
            case ARRAY:
                return TokenType.ARRAY_START;
            case END_OF_DOCUMENT:
                // ARRAY_END is handled earlier
                return TokenType.OBJECT_END;
            case BOOLEAN:
                return TokenType.BOOLEAN;
            case STRING:
                return TokenType.STRING;
            case INT32:
                return TokenType.INT;
            case INT64:
                return TokenType.LONG;
            case DOUBLE:
                return TokenType.DOUBLE;
            case BINARY:
                return TokenType.BINARY;
            case NULL:
                return TokenType.NULL;
            default:
                return TokenType.UNKNOWN;
        }
    }

    @Override
    public String readName() {
        checkToken(peek(), TokenType.NAME);
        return reader.readName();
    }

    @Override
    public void readObjectStart() {
        checkToken(peek(), TokenType.OBJECT_START);
        reader.readStartDocument();
    }

    @Override
    public void readObjectEnd() {
        checkToken(peek(), TokenType.OBJECT_END);
        reader.readEndDocument();
    }

    @Override
    public void readArrayStart() {
        checkToken(peek(), TokenType.ARRAY_START);
        reader.readStartArray();
    }

    @Override
    public void readArrayEnd() {
        checkToken(peek(), TokenType.ARRAY_END);
        reader.readEndArray();
    }

    @Override
    public String readString() {
        checkToken(peek(), TokenType.STRING);
        return reader.readString();
    }

    @Override
    public boolean readBoolean() {
        checkToken(peek(), TokenType.BOOLEAN);
        return reader.readBoolean();
    }

    @Override
    public int readInt() {
        checkToken(peek(), TokenType.INT);
        return reader.readInt32();
    }

    @Override
    public long readLong() {
        checkToken(peek(), TokenType.LONG);
        return reader.readInt64();
    }

    @Override
    public double readDouble() {
        checkToken(peek(), TokenType.DOUBLE);
        return reader.readDouble();
    }

    @Override
    public byte[] readBinary() {
        checkToken(peek(), TokenType.BINARY);
        return reader.readBinaryData().getData();
    }

    @Override
    public void readNull() {
        checkToken(peek(), TokenType.NULL);
        reader.readNull();
    }

    @Override
    public void skipValue() {
        checkToken(peek(), type -> type.kind().isValue());
        reader.skipValue();
    }
}
