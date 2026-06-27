package net.roxymc.jserialize.format.bson.token;

import net.roxymc.jserialize.token.AbstractScalarToken;
import net.roxymc.jserialize.token.TokenType;
import org.bson.BsonRegularExpression;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class RegularExpressionToken extends AbstractScalarToken<BsonRegularExpression> {
    private final BsonRegularExpression value;

    public RegularExpressionToken(BsonRegularExpression value) {
        this.value = nonNull(value, "value");
    }

    @Override
    public BsonRegularExpression value() {
        return value;
    }

    @Override
    public TokenType.Valued<BsonRegularExpression> type() {
        return BsonTokenTypes.REGULAR_EXPRESSION;
    }
}
