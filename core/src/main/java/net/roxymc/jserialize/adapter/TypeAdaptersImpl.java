package net.roxymc.jserialize.adapter;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.type.TypeRef;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.AnnotatedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static net.roxymc.jserialize.util.ObjectUtils.nonNull;

final class TypeAdaptersImpl implements TypeAdapters {
    private final Registry<TypeAdapter.Factory, TypeAdapter<?>> typeAdapters;
    private final Registry<KeyAdapter.Factory, KeyAdapter<?>> keyAdapters;

    @SuppressWarnings({"NullableProblems", "DataFlowIssue"}) // IntelliJ has existential issues
    private TypeAdaptersImpl(BuilderImpl builder) {
        this.typeAdapters = new Registry<>(builder.typeAdapters, (factory, type) -> factory.create(type, this));
        this.keyAdapters = new Registry<>(builder.keyAdapters, (factory, type) -> factory.createKey(type, this));
    }

    @Override
    public <T> @Nullable TypeAdapter<T> get(TypeRef<T> type) {
        @SuppressWarnings("unchecked")
        TypeAdapter<T> adapter = (TypeAdapter<T>) typeAdapters.get(nonNull(type, "type")).orElse(null);
        return adapter;
    }

    @Override
    public <T> @Nullable KeyAdapter<T> getKey(TypeRef<T> type) {
        @SuppressWarnings("unchecked")
        KeyAdapter<T> adapter = (KeyAdapter<T>) keyAdapters.get(nonNull(type, "type")).orElse(null);
        return adapter;
    }

    @Override
    public @Nullable <T> TypeAdapter<T> create(TypeRef<T> type, TypeAdapters adapters) {
        for (TypeAdapter.Factory factory : typeAdapters.factories) {
            TypeAdapter<T> adapter = factory.create(type, adapters);

            if (adapter != null) {
                return adapter;
            }
        }

        return null;
    }

    @Override
    public @Nullable <T> KeyAdapter<T> createKey(TypeRef<T> type, TypeAdapters adapters) {
        for (KeyAdapter.Factory factory : keyAdapters.factories) {
            KeyAdapter<T> adapter = factory.createKey(type, adapters);

            if (adapter != null) {
                return adapter;
            }
        }

        return null;
    }

    private static final class Registry<F, A> {
        private final Map<AnnotatedType, Optional<A>> cache = new ConcurrentHashMap<>();
        private final Set<F> factories;
        private final BiFunction<F, TypeRef<?>, @Nullable A> creator;

        private Registry(Set<F> factories, BiFunction<F, TypeRef<?>, @Nullable A> creator) {
            this.factories = Collections.unmodifiableSet(new LinkedHashSet<>(factories));
            this.creator = creator;
        }

        private Optional<A> get(TypeRef<?> type) {
            AnnotatedType targetType = GenericTypeReflector.toCanonicalBoxed(type.getAnnotatedType());

            return cache.computeIfAbsent(targetType, $ -> {
                for (F factory : factories) {
                    A adapter = creator.apply(factory, type);

                    if (adapter != null) {
                        return Optional.of(adapter);
                    }
                }

                return Optional.empty();
            });
        }
    }

    static final class BuilderImpl implements Builder {
        private final Set<TypeAdapter.Factory> typeAdapters = new LinkedHashSet<>();
        private final Set<KeyAdapter.Factory> keyAdapters = new LinkedHashSet<>();

        @Override
        public Builder add(TypeAdapter.Factory factory) {
            typeAdapters.add(nonNull(factory, "factory"));
            return this;
        }

        @Override
        public Builder addKey(KeyAdapter.Factory factory) {
            keyAdapters.add(nonNull(factory, "factory"));
            return this;
        }

        @Override
        public Builder addAll(TypeAdapters adapters) {
            nonNull(adapters, "adapters");

            typeAdapters.add(adapters);
            keyAdapters.add(adapters);
            return this;
        }

        @Override
        public TypeAdapters build() {
            return new TypeAdaptersImpl(this);
        }
    }
}
