package net.roxymc.jserialize.token;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class StringToken extends AbstractValuedToken<String> implements ScalarToken<String> {
    private final String value;

    public StringToken(String value) {
        this.value = nonNull(value, "value");
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public TokenType.Valued<String> type() {
        return TokenTypes.STRING;
    }
}
