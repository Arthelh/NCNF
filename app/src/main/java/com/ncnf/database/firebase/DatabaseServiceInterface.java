package com.ncnf.database.firebase;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DatabaseServiceInterface {


    /**
     * Get a document from Firebase.Firestore as an object of type documentType.
     * @param documentPath Path where the document is stored on Firebase.Firestore
     * @param documentType Class of the object to return
     * @return CompletableFuture encapsulating an object of type documentType found in Firestore at documentPath
     */
    <T> CompletableFuture<T> getDocument(String documentPath, Class<T> documentType);

    /**
     * Store an object of type T on Firebase.Firestore given the path.
     * @param documentPath Path where the document will be stored on Firebase.Firestore
     * @param object Object to be stored on the Firebase.Firestore
     * @param <T> Class of the object
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    <T> CompletableFuture<Boolean> setDocument(String documentPath, T object);

    /**
     * Delete the document stored on Firebase.Firestore.
     * @param documentPath Path where the document is stored on Firebase.Firestore
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    CompletableFuture<Boolean> deleteDocument(String documentPath);

    /**
     * Gdt a particular field from a document stored on the Firebase.Firestore.
     * @param <T> Class of the wanted field
     * @param documentPath Path where the document is stored on Firebase.Firestore
     * @param field Key corresponding to the wanted field
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    <T> CompletableFuture<T> getField(String documentPath, String field);

    /**
     * Update the field's value of the corresponding document.
     * @param documentPath Path where the document is stored on Firebase.Firestore
     * @param field Key corresponding to the field we want to update
     * @param value New value of the field
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    CompletableFuture<Boolean> updateField(String documentPath, String field, Object value);

    /**
     * Add the given value to the array field of the corresponding.
     * @param documentPath Path where the document is stored on Firebase.Firestore
     * @param arrayField Key corresponding to the array field we want to update
     * @param value Value that will be added to the array
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    CompletableFuture<Boolean> updateArrayField(String documentPath, String arrayField, Object value);

    /**
     * Remove the given value to the array field of the corresponding.
     * The method will remove all occurrences of this value in the array or nothing if the array doesn't contain this value.
     * @param documentPath Path where the document is stored on Firebase.Firestore
     * @param arrayField Key corresponding to the array field we want to update
     * @param value Value that will be added to the array
     * @return CompletableFuture containing the Firebase's response : true if successful
     */
    CompletableFuture<Boolean> removeArrayField(String documentPath, String arrayField, Object value);

    /**
     * Get a collection from Firebase.Firestore as list of objects of type documentType.
     * @param collectionPath Path where the collection is stored on Firebase.Firestore
     * @param collectionType Class of the object to return from the collection
     * @return CompletableFuture containing a list of objects with type collectionType from the collection
     */
    <T> CompletableFuture<List<T>> getCollection(String collectionPath, Class<T> collectionType);

    /**
     * Get all objects from a given collection where a particular (string) field contains a particular string.
     * Example : to get all users whose username contains "john", one will do the following call :
     * withFieldContaining(USERS_COLLECTION_KEY, USERNAME_KEY, john, User.class); .
     * @param collectionPath Path where the collection is stored on Firebase.Firestore
     * @param field Key corresponding to the field we want to check
     * @param value String that should be contains in the field
     * @param collectionType Class of the object to return from the collection
     * @return CompletableFuture containing a list of objects with type collectionType that fulfill the condition
     */
    <T> CompletableFuture<List<T>> withFieldContaining(String collectionPath, String field, String value, Class<T> collectionType);

    /**
     * Get all objects from a given collection where an array field contains a particular value.
     * Example : to get all the users that are friend with the user 12, one will do the following call :
     * whereArrayContains(USERS_COLLECTION_KEY, FRIENDS_KEY, 12, User.class); .
     * @param collectionPath Path where the collection is stored on Firebase.Firestore
     * @param arrayField Key corresponding to the array field we want to check
     * @param value Value that should be contained in the array field
     * @param collectionType Class of the object in the collection
     * @param <T> Class of the object contained in the array field
     * @return CompletableFuture containing a list of objects with type collectionType that fulfill the condition
     */
    <T, R> CompletableFuture<List<R>> whereArrayContains(String collectionPath, String arrayField, T value, Class<R> collectionType);

    /**
     * Get all objects from a given collection which have a particular field equal to a given value.
     * @param collectionPath Path where the collection is stored on Firebase.Firestore
     * @param field Key corresponding to the field we want to check
     * @param value Value to which the field should be equal
     * @param collectionType Class of the object in the collection
     * @param <T> Type of the value to check in the field
     * @return CompletableFuture containing a list of objects with type collectionType that fulfill the condition
     */
    <T, R> CompletableFuture<List<R>> whereEqualTo(String collectionPath, String field, T value, Class<R> collectionType);

    /**
     * Get all objects from a given collection which have a particular field equal to one of the given values.
     * This corresponding to the whereEqualTo method but the equality is made between multiple values, not only one
     * @param collectionPath Path where the collection is stored on Firebase.Firestore
     * @param field Key corresponding to the field we want to check
     * @param values List of values to which the field should be equal (for one of them)
     * @param collectionType Class of the object in the collection
     * @param <T> Type of the value to check in the field
     * @return CompletableFuture containing a list of objects with type collectionType that fulfill the condition
     */
    <T, R> CompletableFuture<List<R>> whereIn(String collectionPath, String field, List<T> values, Class<R> collectionType);
}