package com.ncnf.database;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DatabaseServiceInterface {


    /**
     * Gets a document from Firebase.Firestore as an object of type documentType.
     *
     * @param documentPath The path in Firestore leading to the document
     * @param documentType The class of the object to return
     * @return Returns a CompletableFuture encapsulating an object of type documentType found in Firestore at documentPath
     */
    <T> CompletableFuture<T> getDocument(String documentPath, Class<T> documentType);

    <T> CompletableFuture<Boolean> setDocument(String documentPath, T object);

    CompletableFuture<Boolean> deleteDocument(String documentPath);

    <T> CompletableFuture<T> getField(String path, String field);

    CompletableFuture<Boolean> updateField(String path, String field, Object value);

    CompletableFuture<Boolean> updateArrayField(String documentPath, String arrayField, Object value);

    CompletableFuture<Boolean> removeArrayField(String documentPath, String arrayField, Object value);

    <T> CompletableFuture<List<T>> getCollection(String path, Class<T> collectionType);

    <T> CompletableFuture<List<T>> withFieldLike(String path, String Field, String value, Class<T> collectionType);

    <T, R> CompletableFuture<List<R>> whereArrayContains(String collectionPath, String field, T value, Class<R> type);

    <T, R> CompletableFuture<List<R>> whereEqualTo(String collectionPath, String field, T value, Class<R> type);

    <T, R> CompletableFuture<List<R>> whereIn(String path, String field, List<T> values, Class<R> type);

    CompletableFuture<Boolean> addNews(String uuid, String value);
}