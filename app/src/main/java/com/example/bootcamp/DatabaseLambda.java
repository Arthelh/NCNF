package com.example.bootcamp;

import java.util.Map;

public interface DatabaseLambda {
    default void applyAfterLoadSuccess(Map<String, Object> map){}

    default void applyAfterLoadFailure(){}

    default void applyAfterStoreSuccess(){}

    default void applyAfterStoreFailure(){}
}
