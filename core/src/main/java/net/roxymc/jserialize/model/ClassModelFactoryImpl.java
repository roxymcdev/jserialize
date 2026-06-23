package net.roxymc.jserialize.model;

import net.roxymc.jserialize.annotation.JSerializable;
import net.roxymc.jserialize.model.constructor.ConstructorModel;
import net.roxymc.jserialize.model.property.PropertyMap;
import net.roxymc.jserialize.model.resolver.ConstructorResolver;
import net.roxymc.jserialize.model.resolver.PropertiesResolver;
import net.roxymc.jserialize.model.resolver.SimpleConstructorResolver;
import net.roxymc.jserialize.model.resolver.SimplePropertiesResolver;
import net.roxymc.jserialize.type.TypeRef;
import net.roxymc.jserialize.util.TypeUtils;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class ClassModelFactoryImpl implements ClassModel.Factory {
    static final ClassModel.Factory INSTANCE = ClassModel.factoryBuilder().build();

    private final Map<Class<?>, ClassModel<?>> cache = new ConcurrentHashMap<>();

    private final ConstructorResolver constructorResolver;
    private final PropertiesResolver propertiesResolver;
    private final MethodHandles.Lookup methodLookup;

    private ClassModelFactoryImpl(BuilderImpl builder) {
        this.constructorResolver = builder.constructorResolver;
        this.propertiesResolver = builder.propertiesResolver;
        this.methodLookup = builder.methodLookup;
    }

    @Override
    public <T> ClassModel<T> create(TypeRef<? extends T> type) {
        return createUnchecked(type.getRawType());
    }

    @Override
    public <T> ClassModel<T> create(Class<T> type) {
        return createUnchecked(type);
    }

    private <T> ClassModel<T> createUnchecked(Class<?> type) {
        nonNull(type, "type");

        if (TypeUtils.isPrimitive(type)) {
            throw new IllegalArgumentException("type cannot be primitive");
        }

        @SuppressWarnings("unchecked")
        ClassModel<T> classModel = (ClassModel<T>) cache.computeIfAbsent(type, $ -> {
            try {
                return createInternal(type);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create class model of " + type, ex);
            }
        });
        return classModel;
    }

    private <T> ClassModel<T> createInternal(Class<T> type) throws IllegalAccessException {
        MethodHandles.Lookup methodLookup = MethodHandles.privateLookupIn(type, this.methodLookup);
        JSerializable annotation = type.getDeclaredAnnotation(JSerializable.class);

        ConstructorModel constructor = null;
        if (annotation == null || !annotation.mutateOnly()) {
            ConstructorModel.Builder builder = ConstructorModel.builder();

            constructorResolver.resolveConstructor(type, builder);

            constructor = builder.build(methodLookup);
        }

        PropertyMap properties;
        {
            PropertyMap.Builder builder = PropertyMap.builder();

            propertiesResolver.resolveProperties(type, builder);

            if (constructor != null) {
                propertiesResolver.resolveProperties(constructor, builder);
            }

            properties = builder.build(methodLookup);
        }

        return new ClassModelImpl<>(type, constructor, properties);
    }

    static final class BuilderImpl implements ClassModel.Factory.Builder {
        private ConstructorResolver constructorResolver = SimpleConstructorResolver.INSTANCE;
        private PropertiesResolver propertiesResolver = SimplePropertiesResolver.INSTANCE;
        private MethodHandles.Lookup methodLookup = MethodHandles.lookup();

        @Override
        public Builder constructorResolver(ConstructorResolver constructorResolver) {
            this.constructorResolver = nonNull(constructorResolver, "constructorResolver");
            return this;
        }

        @Override
        public Builder propertiesResolver(PropertiesResolver propertiesResolver) {
            this.propertiesResolver = nonNull(propertiesResolver, "propertiesResolver");
            return this;
        }

        @Override
        public Builder methodLookup(MethodHandles.Lookup methodLookup) {
            this.methodLookup = nonNull(methodLookup, "methodLookup");
            return this;
        }

        @Override
        public ClassModel.Factory build() {
            return new ClassModelFactoryImpl(this);
        }
    }
}
