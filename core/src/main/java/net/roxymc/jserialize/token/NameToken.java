package net.roxymc.jserialize.token;

import net.roxymc.jserialize.Writer;

import java.io.IOException;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class NameToken implements Token {
    private final String value;

    public NameToken(String value) {
        this.value = nonNull(value, "value");
    }

    public String value() {
        return value;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.writeString(value());
    }

    @Override
    public TokenType type() {
        return TokenType.NAME;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof NameToken)) {
            return false;
        }

        NameToken that = (NameToken) obj;
        return this.value.equals(that.value);
    }
}
