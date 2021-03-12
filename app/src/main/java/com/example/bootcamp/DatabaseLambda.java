package com.example.bootcamp;

import java.util.Map;

public interface DatabaseLambda {
    public void applyAfterLoad(Map<String, Object> map);
}
