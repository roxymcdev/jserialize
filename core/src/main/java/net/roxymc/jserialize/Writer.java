package net.roxymc.jserialize;

import java.io.IOException;

public interface Writer {
    void writeName(String name) throws IOException;

    void writeObjectStart() throws IOException;

    void writeObjectEnd() throws IOException;

    void writeArrayStart() throws IOException;

    void writeArrayEnd() throws IOException;

    void writeString(String value) throws IOException;

    void writeBoolean(boolean value) throws IOException;

    void writeInt(int value) throws IOException;

    void writeLong(long value) throws IOException;

    void writeDouble(double value) throws IOException;

    void writeBinary(byte[] value) throws IOException;

    void writeNull() throws IOException;
}
