package net.roxymc.jserialize.adapter;

public interface KeyEncoder<T> {
    String encode(T value);
}
