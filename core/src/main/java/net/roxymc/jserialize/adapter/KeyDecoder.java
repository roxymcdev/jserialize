package net.roxymc.jserialize.adapter;

public interface KeyDecoder<T> {
    T decode(String value);
}
