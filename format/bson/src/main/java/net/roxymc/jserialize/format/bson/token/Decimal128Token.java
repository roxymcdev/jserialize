package net.roxymc.jserialize.format.bson.token;

import net.roxymc.jserialize.token.AbstractScalarToken;
import net.roxymc.jserialize.token.TokenType;
import org.bson.types.Decimal128;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class Decimal128Token extends AbstractScalarToken<Decimal128> {
    private final Decimal128 value;

    public Decimal128Token(Decimal128 value) {
        this.value = nonNull(value, "value");
    }

    @Override
    public Decimal128 value() {
        return value;
    }

    @Override
    public TokenType.Valued<Decimal128> type() {
        return BsonTokenTypes.DECIMAL_128;
    }
}
