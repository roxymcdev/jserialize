package net.roxymc.jserialize.format.bson.token;

import net.roxymc.jserialize.token.AbstractScalarToken;
import net.roxymc.jserialize.token.TokenType;
import org.bson.types.ObjectId;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

public final class ObjectIdToken extends AbstractScalarToken<ObjectId> {
    private final ObjectId value;

    public ObjectIdToken(ObjectId value) {
        this.value = nonNull(value, "value");
    }

    @Override
    public ObjectId value() {
        return value;
    }

    @Override
    public TokenType.Valued<ObjectId> type() {
        return BsonTokenTypes.OBJECT_ID;
    }
}
