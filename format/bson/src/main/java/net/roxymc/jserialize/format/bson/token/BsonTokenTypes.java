package net.roxymc.jserialize.format.bson.token;

import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.TokenType.Kind;
import org.bson.BsonBinary;
import org.bson.BsonDbPointer;
import org.bson.BsonRegularExpression;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

import static net.roxymc.jserialize.token.TokenType.nonValued;
import static net.roxymc.jserialize.token.TokenType.valued;

public final class BsonTokenTypes {
    public static final TokenType.Valued<BsonBinary> BINARY = valued("bson_binary", Kind.SCALAR, BsonBinaryToken::new);
    public static final TokenType.NonValued UNDEFINED = nonValued("bson_undefined", Kind.NULL, () -> BsonTokens.UNDEFINED);
    public static final TokenType.Valued<ObjectId> OBJECT_ID = valued("bson_object_id", Kind.SCALAR, ObjectIdToken::new);
    public static final TokenType.Valued<Long> DATE_TIME = valued("bson_date_time", Kind.SCALAR, DateTimeToken::new);
    public static final TokenType.Valued<BsonRegularExpression> REGULAR_EXPRESSION = valued("bson_regular_expression", Kind.SCALAR, RegularExpressionToken::new);
    public static final TokenType.Valued<BsonDbPointer> DB_POINTER = valued("bson_db_pointer", Kind.SCALAR, DbPointerToken::new);
    public static final TokenType.Valued<String> JAVA_SCRIPT = valued("bson_java_script", Kind.SCALAR, JavaScriptToken::new);
    public static final TokenType.Valued<String> SYMBOL = valued("bson_symbol", Kind.SCALAR, SymbolToken::new);
    public static final TokenType.Valued<String> JAVA_SCRIPT_WITH_SCOPE = valued("bson_java_script_with_scope", Kind.SCALAR, JavaScriptWithScopeToken::new);
    public static final TokenType.Valued<Decimal128> DECIMAL_128 = valued("bson_decimal_128", Kind.SCALAR, Decimal128Token::new);
    public static final TokenType.NonValued MIN_KEY = nonValued("bson_min_key", Kind.NULL, () -> BsonTokens.MIN_KEY);
    public static final TokenType.NonValued MAX_KEY = nonValued("bson_max_key", Kind.NULL, () -> BsonTokens.MAX_KEY);

    private BsonTokenTypes() {
    }
}
