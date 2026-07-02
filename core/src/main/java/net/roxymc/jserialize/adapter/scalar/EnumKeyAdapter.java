package net.roxymc.jserialize.adapter.scalar;

import net.roxymc.jserialize.adapter.AbstractKeyAdapter;
import net.roxymc.jserialize.adapter.KeyAdapter;
import net.roxymc.jserialize.type.TypeRef;
import net.roxymc.jserialize.util.TypeUtils;

public final class EnumKeyAdapter<E extends Enum<E>> extends AbstractKeyAdapter<E> {
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final Factory FACTORY = Factory.<Enum>where(TypeUtils::isEnum, (type, $) -> new EnumKeyAdapter(type));

    private final TypeRef<E> enumType;
    private final Class<E> enumClass;

    @SuppressWarnings("unchecked")
    private EnumKeyAdapter(TypeRef<E> enumType) {
        this.enumType = enumType;
        this.enumClass = (Class<E>) enumType.getRawType();
    }

    public static KeyAdapter.Factory factory() {
        return FACTORY;
    }

    @Override
    protected E parse(String value) {
        return Enum.valueOf(enumClass, value);
    }

    @Override
    public TypeRef<? extends E> type() {
        return enumType;
    }
}
