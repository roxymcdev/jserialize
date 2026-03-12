package net.roxymc.jserialize.format.bson;

import net.roxymc.jserialize.AbstractWriter;
import org.bson.BsonBinary;
import org.bson.BsonWriter;

final class BsonWriterAdapter extends AbstractWriter {
    final BsonWriter writer;

    BsonWriterAdapter(BsonWriter writer) {
        this.writer = writer;
    }

    @Override
    public void writeName(String name) {
        writer.writeName(name);
    }

    @Override
    public void writeObjectStart() {
        writer.writeStartDocument();
    }

    @Override
    public void writeObjectEnd() {
        writer.writeEndDocument();
    }

    @Override
    public void writeArrayStart() {
        writer.writeStartArray();
    }

    @Override
    public void writeArrayEnd() {
        writer.writeEndArray();
    }

    @Override
    public void writeString(String value) {
        writer.writeString(value);
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

    @Override
    public void writeBinary(byte[] value) {
        writer.writeBinaryData(new BsonBinary(value));
    }

    @Override
    public void writeNull() {
        writer.writeNull();
    }
}
