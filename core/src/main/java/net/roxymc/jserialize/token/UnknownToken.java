package net.roxymc.jserialize.token;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class UnknownToken extends AbstractScalarToken<Object> {
    private final Object value;

    public UnknownToken(Object value) {
        this.value = nonNull(value, "value");
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public TokenType type() {
        return TokenType.UNKNOWN;
    }
}
