package com.ncnf.database;

import android.os.Build;
import android.util.DebugUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.DEBUG_TAG;


public class DatabaseService implements DatabaseServiceInterface {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final DatabaseService instance = new DatabaseService();


    public static DatabaseService getInstance(){
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public CompletableFuture<DatabaseResponse> getData(String path) {
        CompletableFuture<DatabaseResponse> futureResponse = new CompletableFuture<>();
        Log.d(DEBUG_TAG, "Launching query");

        this.db.document(path).get().addOnCompleteListener(task -> {
            try {
                if(task.getException() != null){
                }
                futureResponse.complete(new DatabaseResponse(task.isSuccessful(), task.getResult().getData(), task.getException()));
            } catch (Exception e){
                Log.d(DEBUG_TAG, "Unable to load data from DB", task.getException());
                futureResponse.complete(new DatabaseResponse(false, null, e));
            }
        });
        Log.d(DEBUG_TAG, "Query launched");

        return futureResponse;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<DatabaseResponse> delete(String path){
        CompletableFuture<DatabaseResponse> futureResponse = new CompletableFuture<>();

        this.db.document(path).delete().addOnCompleteListener(task -> {
            try {
                futureResponse.complete(new DatabaseResponse(false, task.getResult(), task.getException()));
            } catch (Exception e){
                Log.d(DEBUG_TAG, "Unable to load data from DB", task.getException());
                futureResponse.complete(new DatabaseResponse(false, null, e));
            }
        });

        return futureResponse;
    }
}
