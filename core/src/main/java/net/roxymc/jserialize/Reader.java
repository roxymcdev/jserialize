package net.roxymc.jserialize;

import net.roxymc.jserialize.token.TokenType;

import java.io.IOException;

public interface Reader {
    TokenType peek() throws IOException;

    String readName() throws IOException;

    void readObjectStart() throws IOException;

    void readObjectEnd() throws IOException;

    void readArrayStart() throws IOException;

    void readArrayEnd() throws IOException;

    String readString() throws IOException;

    boolean readBoolean() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    double readDouble() throws IOException;

    byte[] readBinary() throws IOException;

    void readNull() throws IOException;

    void skipValue() throws IOException;
}
