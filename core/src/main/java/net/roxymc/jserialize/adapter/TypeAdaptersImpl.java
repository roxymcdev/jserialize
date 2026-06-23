package net.roxymc.jserialize.adapter;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.type.TypeToken;
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
        this.keyAdapters = new Registry<>(builder.keyAdapters, (factory, type) -> factory.create(type, this));
    }

    @Override
    public <T> @Nullable TypeAdapter<T> get(TypeToken<T> type) {
        @SuppressWarnings("unchecked")
        TypeAdapter<T> adapter = (TypeAdapter<T>) typeAdapters.get(nonNull(type, "type")).orElse(null);
        return adapter;
    }

    @Override
    public @Nullable <T> KeyAdapter<T> getKey(TypeToken<T> type) {
        @SuppressWarnings("unchecked")
        KeyAdapter<T> adapter = (KeyAdapter<T>) keyAdapters.get(nonNull(type, "type")).orElse(null);
        return adapter;
    }

    private static final class Registry<F, A> {
        private final Map<AnnotatedType, Optional<A>> cache = new ConcurrentHashMap<>();
        private final Set<F> factories;
        private final BiFunction<F, TypeToken<?>, @Nullable A> creator;

        private Registry(Set<F> factories, BiFunction<F, TypeToken<?>, @Nullable A> creator) {
            this.factories = Collections.unmodifiableSet(new LinkedHashSet<>(factories));
            this.creator = creator;
        }

        private Optional<A> get(TypeToken<?> type) {
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
            TypeAdaptersImpl adaptersImpl = (TypeAdaptersImpl) nonNull(adapters, "adapters");

            typeAdapters.addAll(adaptersImpl.typeAdapters.factories);
            keyAdapters.addAll(adaptersImpl.keyAdapters.factories);

            return this;
        }

        @Override
        public TypeAdapters build() {
            return new TypeAdaptersImpl(this);
        }
    }
}
