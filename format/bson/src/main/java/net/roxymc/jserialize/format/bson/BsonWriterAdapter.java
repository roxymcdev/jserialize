package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.AbstractWriter;
import net.roxymc.jserialize.format.TokenTypeInfo;
import net.roxymc.jserialize.token.TokenType;
import org.bson.BsonReader;
import org.bson.BsonWriter;

import java.io.IOException;

final class BsonWriterAdapter extends AbstractWriter {
    final BsonWriter writer;

    BsonWriterAdapter(BsonWriter writer) {
        this.writer = writer;
    }

    @Override
    public void write(TokenType.NonValued tokenType) throws IOException {
        TokenTypeInfo.NonValued<BsonReader, BsonWriter> info = BsonUtils.TOKEN_TYPES.get(tokenType);
        if (info == null) {
            throw notSupported(tokenType);
        }

        info.write(writer);
    }

    @Override
    public <T> void write(TokenType.Valued<T> tokenType, T value) throws IOException {
        TokenTypeInfo.Valued<BsonReader, BsonWriter, T> info = BsonUtils.TOKEN_TYPES.get(tokenType);
        if (info == null) {
            throw notSupported(tokenType);
        }

        info.write(writer, value);
    }

    @Override
    public void writeBoolean(boolean value) {
        writer.writeBoolean(value);
    }

    @Override
    public void writeInt(int value) {
        writer.writeInt32(value);
    }

    @Override
    public void writeLong(long value) {
        writer.writeInt64(value);
    }

    @Override
    public void writeDouble(double value) {
        writer.writeDouble(value);
    }
}
