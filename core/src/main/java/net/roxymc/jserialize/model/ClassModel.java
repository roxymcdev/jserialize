package net.roxymc.jserialize.model;

import net.roxymc.jserialize.model.constructor.ConstructorModel;
import net.roxymc.jserialize.model.property.PropertyMap;
import net.roxymc.jserialize.model.resolver.ConstructorResolver;
import net.roxymc.jserialize.model.resolver.PropertiesResolver;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface ClassModel<T> {
    static Factory factory() {
        return ClassModelFactoryImpl.INSTANCE;
    }

    static Factory.Builder factoryBuilder() {
        return new ClassModelFactoryImpl.BuilderImpl();
    }

    Class<T> clazz();

    @Nullable ConstructorModel constructor();

    PropertyMap properties();

    @ApiStatus.NonExtendable
    interface Factory {
        <T> ClassModel<T> create(Class<T> clazz) throws IllegalAccessException;

        @ApiStatus.NonExtendable
        interface Builder {
            Builder constructorDiscoverer(ConstructorResolver constructorResolver);

            Builder propertyDiscoverer(PropertiesResolver propertiesResolver);

            Factory build();
        }
    }
}
