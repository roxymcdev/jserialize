package net.roxymc.jserialize.token.reader;

import net.roxymc.jserialize.AbstractReader;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.TokenTypes;
import net.roxymc.jserialize.token.ValuedToken;

import java.io.IOException;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class TokenReader<V> extends AbstractReader {
    private final Tokenizer<V> tokenizer;

    public TokenReader(Tokenizer<V> tokenizer) {
        this.tokenizer = nonNull(tokenizer, "tokenizer");
    }

    public Tokenizer<V> tokenizer() {
        return tokenizer;
    }

    @Override
    public TokenType peek() {
        if (!tokenizer.hasNext()) {
            return TokenTypes.END;
        }

        return tokenizer.peek();
    }

    @Override
    public void read(TokenType.NonValued tokenType) throws IOException {
        checkToken(peek(), tokenType);

        tokenizer.next();
    }

    @Override
    public <T> T read(TokenType.Valued<T> tokenType) throws IOException {
        checkToken(peek(), tokenType);

        @SuppressWarnings("unchecked")
        ValuedToken<T> token = (ValuedToken<T>) tokenizer.next();
        return token.value();
    }

    @Override
    public void skipValue() {
        tokenizer.skipValue();
    }
}
