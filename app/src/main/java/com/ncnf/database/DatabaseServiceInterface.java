package com.ncnf.database;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DatabaseServiceInterface {

    CompletableFuture<DatabaseResponse> setDocument(String path, Map<String, Object> fields);

    CompletableFuture<DatabaseResponse> updateField(String path, String field, Object value);

    CompletableFuture<DatabaseResponse> getField(String path, String field);

    CompletableFuture<DatabaseResponse> getData(String path);
}
