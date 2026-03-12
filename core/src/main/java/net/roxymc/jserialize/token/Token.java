package net.roxymc.jserialize.token;

import net.roxymc.jserialize.Writer;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;

@ApiStatus.NonExtendable
public interface Token {
    Token OBJECT_START = new TokenImpl(TokenType.OBJECT_START, Writer::writeObjectStart);
    Token OBJECT_END = new TokenImpl(TokenType.OBJECT_END, Writer::writeObjectEnd);
    Token ARRAY_START = new TokenImpl(TokenType.ARRAY_START, Writer::writeArrayStart);
    Token ARRAY_END = new TokenImpl(TokenType.ARRAY_END, Writer::writeArrayEnd);

    default void write(Writer writer) throws IOException {
        throw new UnsupportedOperationException(type() + " does not support this operation");
    }

    TokenType type();
}
