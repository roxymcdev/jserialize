package net.roxymc.jserialize.format.bson.token;

import net.roxymc.jserialize.token.AbstractValuedToken;
import net.roxymc.jserialize.token.TokenType;

public final class DateTimeToken extends AbstractValuedToken<Long> {
    private final long value;

    public DateTimeToken(long value) {
        this.value = value;
    }

    public long longValue() {
        return value;
    }

    @Override
    public Long value() {
        return value;
    }

    @Override
    public TokenType.Valued<Long> type() {
        return BsonTokenTypes.DATE_TIME;
    }
}
