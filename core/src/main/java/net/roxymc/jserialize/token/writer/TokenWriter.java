package net.roxymc.jserialize.token.writer;

import net.roxymc.jserialize.Writer;
import net.roxymc.jserialize.token.TokenType;

import java.io.IOException;

public final class TokenWriter<V> implements Writer {
    private final Detokenizer<V> detokenizer;

    public TokenWriter(Detokenizer<V> detokenizer) {
        this.detokenizer = detokenizer;
    }

    public Detokenizer<V> detokenizer() {
        return detokenizer;
    }

    @Override
    public void write(TokenType.NonValued tokenType) throws IOException {
        detokenizer.accept(tokenType.create());
    }

    @Override
    public <T> void write(TokenType.Valued<T> tokenType, T value) throws IOException {
        detokenizer.accept(tokenType.create(value));
    }
}
