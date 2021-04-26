package com.ncnf.database;

import android.app.VoiceInteractor;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ncnf.database.builder.DatabaseObjectBuilder;
import com.ncnf.database.builder.EventBuilder;
import com.ncnf.database.builder.UserBuilder;
import com.ncnf.event.Event;
import com.ncnf.event.PrivateEvent;
import com.ncnf.event.PublicEvent;
import com.ncnf.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static com.ncnf.Utils.DEBUG_TAG;

public class DatabaseService implements DatabaseServiceInterface {

    private final FirebaseFirestore db;
    private final Map<Class, DatabaseObjectBuilder> registry = new HashMap<>();

    @Inject
    public DatabaseService(){
        this.db = FirebaseFirestore.getInstance();
        initMap();
    }

    protected DatabaseService(FirebaseFirestore db)  {
        this.db = db;
        initMap();
    }

    private void initMap(){
        registry.put(User.class, new UserBuilder());
        registry.put(PublicEvent.class, new EventBuilder());
        registry.put(PrivateEvent.class, new EventBuilder());
    }

    @Override
    public <T> CompletableFuture<T> getField(String documentPath, String field){
        CompletableFuture<T> futureResponse = new CompletableFuture<>();

        this.db.document(documentPath).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                T result = (T) task.getResult().get(field);
                futureResponse.complete(result);
            } else {
                futureResponse.completeExceptionally(task.getException());
            }
        });
        return futureResponse;
    }

    @Override
    public CompletableFuture<Boolean> updateField(String documentPath, String field, Object value) {
        CompletableFuture<Boolean> futureResponse = new CompletableFuture<>();

        this.db.document(documentPath).update(FieldPath.of(field), value).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                futureResponse.complete(true);
            } else {
                futureResponse.completeExceptionally(task.getException());
            }
        });

        return futureResponse;
    }

    @Override
    public <T> CompletableFuture<T> getDocument(String documentPath, Class<T> documentType){
        CompletableFuture<T> futureResponse = new CompletableFuture<>();

        this.db.document(documentPath).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = (DocumentSnapshot) task.getResult();

                T result = (T) registry.get(documentType).toObject(document.getId(), document.getData());
                futureResponse.complete(result);
            } else {
                futureResponse.completeExceptionally(task.getException());
            }
        });

        return futureResponse;
    }

    @Override
    public <T> CompletableFuture<Boolean> setDocument(String documentPath, T object) {
        CompletableFuture<Boolean> futureResponse = new CompletableFuture<>();

        Map<String, Object> data = registry.get(object.getClass()).toMap(object);

        this.db.document(documentPath).set(data).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                futureResponse.complete(true);
            } else {
                futureResponse.completeExceptionally(task.getException());
            }
        });

        return futureResponse;
    }


    @Override
    public <T> CompletableFuture<List<T>> getCollection(String collectionPath, Class<T> collectionType) {
        CompletableFuture<List<T>> futureResponse = new CompletableFuture<>();

        this.db.collection(collectionPath).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<T> result = new ArrayList<>();
                for(QueryDocumentSnapshot document : task.getResult()){
                    result.add((T) registry.get(collectionType).toObject(document.getId(), document.getData()));
                }
                futureResponse.complete(result);
            } else {
                futureResponse.completeExceptionally(task.getException());
            }
        });

        return futureResponse;
    }

    @Override
    public <T> CompletableFuture<List<T>> withFieldLike(String collectionPath, String field, String value, Class<T> collectionType) {
        CompletableFuture<List<T>> futureResponse = new CompletableFuture<>();

        this.db.collection(collectionPath).orderBy(field).startAt(value).endAt(value + "\uf8ff").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<T> result = new ArrayList<>();
                for(QueryDocumentSnapshot document : task.getResult()){
                    result.add((T) registry.get(collectionType).toObject(document.getId(), document.getData()));
                }

                futureResponse.complete(result);
            } else {
                futureResponse.completeExceptionally(task.getException());
            }
        });

        return futureResponse;
    }

    @Override
    public <T, R> CompletableFuture<List<R>> whereArrayContains(String collectionPath, String field, T value, Class<R> type) {
        CompletableFuture<List<R>> futureResponse = new CompletableFuture<>();

        this.db.collection(collectionPath).whereArrayContains(field, value).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<R> result = new ArrayList<>();
                for(QueryDocumentSnapshot document : task.getResult()){
                    result.add((R) registry.get(type).toObject(document.getId(), document.getData()));
                }

                futureResponse.complete(result);
            } else {
                futureResponse.completeExceptionally(task.getException());
            }
        });

        return futureResponse;
    }

    @Override
    public <T, R> CompletableFuture<List<R>> whereEqualTo(String collectionPath, String field, T value, Class<R> type) {
        CompletableFuture<List<R>> futureResponse = new CompletableFuture<>();

        this.db.collection(collectionPath).whereEqualTo(field, value).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<R> result = new ArrayList<>();
                for(QueryDocumentSnapshot document : task.getResult()){
                    result.add((R) registry.get(type).toObject(document.getId(), document.getData()));
                }

                futureResponse.complete(result);
            } else {
                futureResponse.completeExceptionally(task.getException());
            }
        });

        return futureResponse;
    }

    @Override
    public <T, R> CompletableFuture<List<R>> whereIn(String collectionPath, String field, List<T> values, Class<R> type) {
        if(values.isEmpty()){
            CompletableFuture<List<R>> futureResponse = new CompletableFuture<>();
            futureResponse.complete(new ArrayList<>());
            return futureResponse;
        }

        List<CompletableFuture<List<R>>> futures = values
                .stream()
                .map(value -> whereEqualTo(collectionPath, field, value, type))
                .collect(Collectors.toList());

        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture<?>[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    @Override
    public CompletableFuture<Boolean> deleteDocument(String documentPath){
        CompletableFuture<Boolean> futureResponse = new CompletableFuture<>();

        this.db.document(documentPath).delete().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                futureResponse.complete(true);
            } else {
                futureResponse.completeExceptionally(task.getException());
            }
        });

        return futureResponse;
    }

    @Override
    public CompletableFuture<Boolean> updateArrayField(String documentPath, String arrayField, Object value){
        return this.updateField(documentPath, arrayField, FieldValue.arrayUnion(value));
    }

    @Override
    public CompletableFuture<Boolean> removeArrayField(String documentPath, String arrayField, Object value){
        return this.updateField(documentPath, arrayField, FieldValue.arrayRemove(value));
    }



}

