package net.roxymc.jserialize.token;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.util.IOConsumer;

import java.io.IOException;

final class TokenImpl implements Token {
    private final TokenType type;
    private final IOConsumer<Writer> write;

    TokenImpl(TokenType type, IOConsumer<Writer> write) {
        this.type = type;
        this.write = write;
    }

    @Override
    public void write(Writer writer) throws IOException {
        write.accept(writer);
    }

    @Override
    public TokenType type() {
        return type;
    }
}
