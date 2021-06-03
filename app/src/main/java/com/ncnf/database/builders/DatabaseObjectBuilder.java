package com.ncnf.database.builders;

import java.util.Map;

public abstract class DatabaseObjectBuilder<T> {

    /**
     * Creates an object T with all its attributes instantiated with the value from the map
     * @param uuid object's unique identifier
     * @param data map containing the object attributes
     * @return Created object
     */
    public abstract T toObject(String uuid, Map<String, Object> data);

    /**
     * Transform an object T to a map containing all its attributes, ready to be stored in the database
     * @param object object to convert into a map
     * @return Created map
     */
    public abstract Map<String, Object> toMap(T object);
}
