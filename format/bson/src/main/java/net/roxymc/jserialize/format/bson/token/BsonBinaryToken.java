package net.roxymc.jserialize.format.bson.token;

import net.roxymc.jserialize.token.AbstractScalarToken;
import net.roxymc.jserialize.token.TokenType;
import org.bson.BsonBinary;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class BsonBinaryToken extends AbstractScalarToken<BsonBinary> {
    private final BsonBinary value;

    public BsonBinaryToken(BsonBinary value) {
        this.value = nonNull(value, "value");
    }

    @Override
    public BsonBinary value() {
        return value;
    }

    @Override
    public TokenType.Valued<BsonBinary> type() {
        return BsonTokenTypes.BINARY;
    }
}
