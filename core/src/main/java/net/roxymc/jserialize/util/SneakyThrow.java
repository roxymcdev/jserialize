package net.roxymc.jserialize.util;

public final class SneakyThrow {
    private SneakyThrow() {
    }

    public static <X extends Throwable> RuntimeException sneakyThrow(Throwable ex) throws X {
        //noinspection unchecked
        throw (X) ex;
    }
}
