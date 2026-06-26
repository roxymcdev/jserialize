package net.roxymc.jserialize.format.bson.token;

import net.roxymc.jserialize.token.AbstractValuedToken;
import net.roxymc.jserialize.token.TokenType;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class SymbolToken extends AbstractValuedToken<String> {
    private final String value;

    public SymbolToken(String value) {
        this.value = nonNull(value, "value");
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public TokenType.Valued<String> type() {
        return BsonTokenTypes.SYMBOL;
    }
}
