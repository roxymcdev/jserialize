package net.roxymc.jserialize.token;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class StringToken extends AbstractScalarToken<String> {
    private final String value;

    public StringToken(String value) {
        this.value = nonNull(value, "value");
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.writeString(value());
    }

    @Override
    public TokenType type() {
        return TokenType.STRING;
    }
}
