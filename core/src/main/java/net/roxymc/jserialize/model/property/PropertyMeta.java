package net.roxymc.jserialize.model.property;

import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.AnnotatedElement;

@ApiStatus.NonExtendable
public interface PropertyMeta {
    @ApiStatus.Internal
    static PropertyMeta of(AnnotatedElement element) {
        return of(element, false);
    }

    @ApiStatus.Internal
    static PropertyMeta of(AnnotatedElement element, boolean injectParent) {
        return new PropertyMetaImpl(element, injectParent);
    }

    boolean id();

    boolean required();

    boolean writeNull();

    boolean mutate();

    boolean extra();

    boolean injectParent();

    default boolean shouldDeserialize() {
        return !(extra() || injectParent());
    }
}
