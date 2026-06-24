package net.roxymc.jserialize;

import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.TokenTypes;

import java.io.IOException;

public interface Reader {
    TokenType peek() throws IOException;

    void read(TokenType.NonValued tokenType) throws IOException;

    <T> T read(TokenType.Valued<T> tokenType) throws IOException;

    default String readName() throws IOException {
        return read(TokenTypes.NAME);
    }

    default void readObjectStart() throws IOException {
        read(TokenTypes.OBJECT_START);
    }

    default void readObjectEnd() throws IOException {
        read(TokenTypes.OBJECT_END);
    }

    default void readArrayStart() throws IOException {
        read(TokenTypes.ARRAY_START);
    }

    default void readArrayEnd() throws IOException {
        read(TokenTypes.ARRAY_END);
    }

    default String readString() throws IOException {
        return read(TokenTypes.STRING);
    }

    default boolean readBoolean() throws IOException {
        return read(TokenTypes.BOOLEAN);
    }

    default int readInt() throws IOException {
        return read(TokenTypes.INT);
    }

    default long readLong() throws IOException {
        return read(TokenTypes.LONG);
    }

    default double readDouble() throws IOException {
        return read(TokenTypes.DOUBLE);
    }

    default byte[] readBinary() throws IOException {
        return read(TokenTypes.BINARY);
    }

    default void readNull() throws IOException {
        read(TokenTypes.NULL);
    }

    void skipValue() throws IOException;
}
