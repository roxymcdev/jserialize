package net.roxymc.jserialize.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public final class VarHandles {
    private VarHandles() {
    }

    public static VarHandle find(Class<?> clazz, String name, Class<?> type) {
        try {
            return MethodHandles.privateLookupIn(clazz, MethodHandles.lookup()).findVarHandle(clazz, name, type);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw SneakyThrow.sneakyThrow(ex);
        }
    }
}
