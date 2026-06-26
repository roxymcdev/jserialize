package net.roxymc.jserialize.format.bson.token;

import net.roxymc.jserialize.token.AbstractValuedToken;
import net.roxymc.jserialize.token.TokenType;
import org.bson.BsonDbPointer;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class DbPointerToken extends AbstractValuedToken<BsonDbPointer> {
    private final BsonDbPointer value;

    public DbPointerToken(BsonDbPointer value) {
        this.value = nonNull(value, "value");
    }

    @Override
    public BsonDbPointer value() {
        return value;
    }

    @Override
    public TokenType.Valued<BsonDbPointer> type() {
        return BsonTokenTypes.DB_POINTER;
    }
}
