package net.roxymc.jserialize.token;

import net.roxymc.jserialize.Writer;
import org.jspecify.annotations.Nullable;

import java.io.IOException;

final class NullToken extends AbstractScalarToken<@Nullable Object> {
    static final NullToken INSTANCE = new NullToken();

    private NullToken() {
    }

    @Override
    public @Nullable Object value() {
        return null;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.writeNull();
    }

    @Override
    public TokenType type() {
        return TokenType.NULL;
    }
}
