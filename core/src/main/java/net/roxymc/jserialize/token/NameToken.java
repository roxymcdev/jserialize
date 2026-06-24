package net.roxymc.jserialize.token;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class NameToken extends AbstractValuedToken<String> {
    private final String value;

    public NameToken(String value) {
        this.value = nonNull(value, "value");
    }

    public String value() {
        return value;
    }

    @Override
    public TokenType.Valued<String> type() {
        return TokenTypes.NAME;
    }
}
