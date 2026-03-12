package net.roxymc.jserialize.adapter;

import io.leangen.geantyref.GenericTypeReflector;
import net.roxymc.jserialize.type.TypeToken;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.AnnotatedType;
import java.util.*;

final class TypeAdaptersImpl implements TypeAdapters {
    private final Map<AnnotatedType, Optional<TypeAdapter<?>>> cache = new HashMap<>();
    private final Set<TypeAdapter.Factory> factories;

    private TypeAdaptersImpl(BuilderImpl builder) {
        this.factories = Collections.unmodifiableSet(new LinkedHashSet<>(builder.factories));
    }

    @Override
    public @Nullable <T> TypeAdapter<T> get(TypeToken<? extends T> type) {
        @SuppressWarnings("unchecked")
        TypeAdapter<T> adapter = (TypeAdapter<T>) get0(type).orElse(null);
        return adapter;
    }

    private Optional<TypeAdapter<?>> get0(TypeToken<?> type) {
        AnnotatedType targetType = GenericTypeReflector.toCanonicalBoxed(type.getAnnotatedType());

        return cache.computeIfAbsent(targetType, $ -> {
            for (TypeAdapter.Factory factory : factories) {
                TypeAdapter<?> adapter = factory.create(type, this);

                if (adapter != null) {
                    return Optional.of(adapter);
                }
            }

            return Optional.empty();
        });
    }

    static final class BuilderImpl implements Builder {
        private final Set<TypeAdapter.Factory> factories = new LinkedHashSet<>();

        @Override
        public Builder add(TypeAdapter.Factory factory) {
            factories.add(factory);
            return this;
        }

        @Override
        public TypeAdapters build() {
            return new TypeAdaptersImpl(this);
        }
    }
}
