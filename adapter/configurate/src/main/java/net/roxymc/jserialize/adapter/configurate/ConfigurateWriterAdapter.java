package net.roxymc.jserialize.adapter.configurate;

import net.roxymc.jserialize.adapter.WriterAdapter;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.lang.reflect.Type;

final class ConfigurateWriterAdapter implements WriterAdapter {
    private final ConfigurationNode rootNode;

    ConfigurateWriterAdapter(ConfigurationNode rootNode) {
        this.rootNode = rootNode;
    }

    @Override
    public void writeProperty(String name, Type type, @Nullable Object value) throws Throwable {
        rootNode.node(name).set(type, value);
    }
}
