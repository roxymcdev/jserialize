package net.roxymc.jserialize.token.writer;

import net.roxymc.jserialize.token.Token;

public interface Detokenizer<V> {
    void accept(Token token);

    V value();
}
