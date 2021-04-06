package com.ncnf.database;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

import static com.ncnf.Utils.*;

public class DatabaseService implements DatabaseServiceInterface {

    private final FirebaseFirestore db;
   
    public DatabaseService(){
        this.db = FirebaseFirestore.getInstance();
    }

    public DatabaseService(FirebaseFirestore db)  {
        this.db = db;
    }

    @Override
    public CompletableFuture<DatabaseResponse> setDocument(String path, Map<String, Object> fields) {
        CompletableFuture<DatabaseResponse> futureResponse = new CompletableFuture<>();
        this.db.document(path).set(fields).addOnCompleteListener(task -> {
           try {
               futureResponse.complete(new DatabaseResponse(task.isSuccessful(), task.getResult(), task.getException()));
           } catch (Exception e){
               Log.d(DEBUG_TAG, "Unable to set document from DB", task.getException());
               futureResponse.complete(new DatabaseResponse(false, null, e));
           }
        });
        return futureResponse;
    }

    @Override
    public CompletableFuture<DatabaseResponse> updateField(String path, String field, Object value) {
        CompletableFuture<DatabaseResponse> futureResponse = new CompletableFuture<>();

        this.db.document(path).update(FieldPath.of(field), value).addOnCompleteListener(task -> {
            try {
                futureResponse.complete(new DatabaseResponse(task.isComplete() && task.isSuccessful(), task.getResult(), task.getException()));
            } catch (Exception e){
                Log.d(DEBUG_TAG, "Unable to update field from DB", task.getException());
                futureResponse.complete(new DatabaseResponse(false, null, e));
            }
        });
        return futureResponse;
    }

    @Override
    public CompletableFuture<DatabaseResponse> getField(String path, String field) {
        return getData(path).thenApply(task -> {
           if(task.isSuccessful()){
               Object obj = ((Map<String, Object>) task.getResult()).get(field);
               return new DatabaseResponse(true, obj, task.getException());
           } else {
               Log.d(DEBUG_TAG, "Unable to load field from DB", task.getException());
               return task;
           }
        });
    }

    @Override
    public CompletableFuture<DatabaseResponse> getData(String path) {
        CompletableFuture<DatabaseResponse> futureResponse = new CompletableFuture<>();

        Log.d(DEBUG_TAG, path);
        this.db.document(path).get().addOnCompleteListener(task -> {
            try {
                if(path.contains("event")){
                    Log.d(DEBUG_TAG, task.getResult().toString());
                }
                futureResponse.complete(new DatabaseResponse(task.isSuccessful(), task.getResult().getData(), task.getException()));
            } catch (Exception e){
                Log.d(DEBUG_TAG, "Unable to load data from DB", task.getException());
                futureResponse.complete(new DatabaseResponse(false, null, e));
            }
        });

        return futureResponse;
    }

    public CompletableFuture<DatabaseResponse> delete(String path){
        CompletableFuture<DatabaseResponse> futureResponse = new CompletableFuture<>();

        this.db.document(path).delete().addOnCompleteListener(task -> {
            try {
                futureResponse.complete(new DatabaseResponse(true, task.getResult(), task.getException()));
            } catch (Exception e){
                Log.d(DEBUG_TAG, "Unable to load data from DB", task.getException());
                futureResponse.complete(new DatabaseResponse(false, null, e));
            }
        });

        return futureResponse;
    }

    public CompletableFuture<DatabaseResponse> updateArrayField(String path, String arrayField, Object value){
        CompletableFuture<DatabaseResponse> futureResponse = new CompletableFuture<>();
        Log.d(DEBUG_TAG, "okok + " + path + " " + arrayField);
        return this.updateField(path, arrayField, FieldValue.arrayUnion(value));
    }
}
