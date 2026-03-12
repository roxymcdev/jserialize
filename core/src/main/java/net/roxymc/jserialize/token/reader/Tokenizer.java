package net.roxymc.jserialize.token.reader;

import net.roxymc.jserialize.token.Token;
import net.roxymc.jserialize.token.TokenType;

import java.util.Iterator;

public interface Tokenizer<V> extends Iterator<Token> {
    @Override
    boolean hasNext();

    TokenType peek();

    @Override
    Token next();

    // returns raw value without tokenization
    V nextValue();

    void skipValue();
}
