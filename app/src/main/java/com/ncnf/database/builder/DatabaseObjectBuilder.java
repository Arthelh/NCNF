package com.ncnf.database.builder;

import java.util.Map;

public abstract class DatabaseObjectBuilder<T> {

    public abstract T toObject(String uuid, Map<String, Object> data);

    public abstract Map<String, Object> toMap(T object);
}
