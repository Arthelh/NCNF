package com.example.bootcamp;

import java.util.Map;

public interface DatabaseLambda {
    void applyAfterLoad(Map<String, Object> map);
}
