package net.roxymc.jserialize.format.configurate;

import net.roxymc.jserialize.token.ScalarToken;
import net.roxymc.jserialize.token.TokenType;
import net.roxymc.jserialize.token.AbstractValueAccessor;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

final class ConfigurationNodeAccessor extends AbstractValueAccessor<ConfigurationNode> {
    static final ConfigurationNodeAccessor INSTANCE = new ConfigurationNodeAccessor();

    private ConfigurationNodeAccessor() {
    }

    @Override
    public String getName(ConfigurationNode value) {
        return String.valueOf(value.key());
    }

    @Override
    public boolean isObject(ConfigurationNode value) {
        return value.isMap();
    }

    @Override
    public boolean isArray(ConfigurationNode value) {
        return value.isList();
    }

    @Override
    public Iterable<? extends ConfigurationNode> getValues(ConfigurationNode value) {
        if (value.isMap()) {
            return value.childrenMap().values();
        }

        if (value.isList()) {
            return value.childrenList();
        }

        throw new IllegalArgumentException("value is a scalar");
    }

    @Override
    public ConfigurationNode objectAppend(ConfigurationNode container, String name) {
        return container.node(name);
    }

    @Override
    public ConfigurationNode listAppend(ConfigurationNode container) {
        return container.appendListNode();
    }

    @Override
    public void setScalar(ConfigurationNode value, @Nullable Object raw) {
        value.raw(raw);
    }

    @Override
    public TokenType getTokenType(ConfigurationNode value) {
        if (value.isMap() || value.isList()) {
            throw new IllegalArgumentException("value is not a scalar");
        }

        return getRawTokenType(value.rawScalar());
    }

    @Override
    public ScalarToken<?> toToken(ConfigurationNode value) {
        if (value.isMap() || value.isList()) {
            throw new IllegalArgumentException("value is not a scalar");
        }

        return toRawToken(value.rawScalar());
    }
}
