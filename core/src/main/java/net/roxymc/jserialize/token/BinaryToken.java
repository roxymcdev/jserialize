package net.roxymc.jserialize.token;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

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
    public void write(Writer writer) throws IOException {
        writer.writeBinary(value());
    }

    @Override
    public TokenType type() {
        return TokenType.BINARY;
    }
}
