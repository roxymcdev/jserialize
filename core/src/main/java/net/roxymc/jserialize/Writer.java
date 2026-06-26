package net.roxymc.jserialize;

import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.TokenTypes;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;

@ApiStatus.NonExtendable
public interface Writer {
    void write(TokenType.NonValued tokenType) throws IOException;

    <T> void write(TokenType.Valued<T> tokenType, T value) throws IOException;

    default void writeName(String name) throws IOException {
        write(TokenTypes.NAME, name);
    }

    default void writeObjectStart() throws IOException {
        write(TokenTypes.OBJECT_START);
    }

    default void writeObjectEnd() throws IOException {
        write(TokenTypes.OBJECT_END);
    }

    default void writeArrayStart() throws IOException {
        write(TokenTypes.ARRAY_START);
    }

    default void writeArrayEnd() throws IOException {
        write(TokenTypes.ARRAY_END);
    }

    default void writeString(String value) throws IOException {
        write(TokenTypes.STRING, value);
    }

    default void writeBoolean(boolean value) throws IOException {
        write(TokenTypes.BOOLEAN, value);
    }

    default void writeInt(int value) throws IOException {
        write(TokenTypes.INT, value);
    }

    default void writeLong(long value) throws IOException {
        write(TokenTypes.LONG, value);
    }

    default void writeDouble(double value) throws IOException {
        write(TokenTypes.DOUBLE, value);
    }

    default void writeBinary(byte[] value) throws IOException {
        write(TokenTypes.BINARY, value);
    }

    default void writeNull() throws IOException {
        write(TokenTypes.NULL);
    }
}
