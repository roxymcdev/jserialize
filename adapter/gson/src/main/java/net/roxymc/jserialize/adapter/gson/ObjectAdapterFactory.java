package net.roxymc.jserialize.adapter.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import net.roxymc.jserialize.model.ClassModel;

public final class ObjectAdapterFactory implements TypeAdapterFactory {
    private final ClassModel.Factory factory;

    public ObjectAdapterFactory(ClassModel.Factory factory) {
        this.factory = factory;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        try {
            @SuppressWarnings("unchecked")
            ClassModel<T> classModel = (ClassModel<T>) factory.create(type.getRawType());

            return new ObjectAdapter<>(classModel, gson);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
