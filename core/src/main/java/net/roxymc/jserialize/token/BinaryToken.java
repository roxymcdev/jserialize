package net.roxymc.jserialize.token;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class BinaryToken extends AbstractScalarToken<byte[]> {
    private final byte[] value;

    public BinaryToken(byte[] value) {
        this.value = nonNull(value, "value").clone();
    }

    @Override
    public byte[] value() {
        return value.clone();
    }

    @Override
    public TokenType.Valued<byte[]> type() {
        return TokenTypes.BINARY;
    }
}
