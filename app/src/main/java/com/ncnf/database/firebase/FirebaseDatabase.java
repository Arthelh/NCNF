package com.ncnf.database.firebase;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ncnf.database.Database;
import com.ncnf.database.builders.DatabaseObjectBuilder;
import com.ncnf.database.builders.DatabaseEventBuilder;
import com.ncnf.database.builders.DatabaseGroupBuilder;
import com.ncnf.database.builders.DatabaseOrganizationBuilder;
import com.ncnf.database.builders.DatabaseUserBuilder;
import com.ncnf.models.Event;
import com.ncnf.models.Group;
import com.ncnf.models.Organization;
import com.ncnf.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static com.ncnf.utilities.StringCodes.GEOHASH_KEY;

public class FirebaseDatabase implements Database {

    private final FirebaseFirestore db;
    private final Map<Class, DatabaseObjectBuilder> registry = new HashMap<>();

    public FirebaseDatabase(){
        this.db = FirebaseFirestore.getInstance();
        initRegistry();
    }

    @Inject
    protected FirebaseDatabase(FirebaseFirestore db)  {
        this.db = db;
        initRegistry();
    }

    /**
     * Initialization of the different builders for our objects
     */
    private void initRegistry(){
        registry.put(Organization.class, new DatabaseOrganizationBuilder());
        registry.put(User.class, new DatabaseUserBuilder());
        registry.put(Event.class, new DatabaseEventBuilder());
        registry.put(Group.class, new DatabaseGroupBuilder());
        registry.put(Organization.class, new DatabaseOrganizationBuilder());
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
                DocumentSnapshot document = task.getResult();

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

        Task<QuerySnapshot> query = this.db.collection(collectionPath).get();
        query.addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<T> result = new ArrayList<>();
                for(DocumentSnapshot document :  task.getResult().getDocuments()){
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
    public <T> CompletableFuture<List<T>> withFieldContaining(String collectionPath, String field, String value, Class<T> collectionType) {
        CompletableFuture<List<T>> futureResponse = new CompletableFuture<>();

        this.db.collection(collectionPath).orderBy(field).startAt(value).endAt(value + "\uf8ff").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<T> result = new ArrayList<>();
                for(DocumentSnapshot document : task.getResult().getDocuments()){
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
    public <T, R> CompletableFuture<List<R>> whereArrayContains(String collectionPath, String arrayField, T value, Class<R> collectionType) {
        CompletableFuture<List<R>> futureResponse = new CompletableFuture<>();

        this.db.collection(collectionPath).whereArrayContains(arrayField, value).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<R> result = new ArrayList<>();
                for(DocumentSnapshot document : task.getResult().getDocuments()){
                    result.add((R) registry.get(collectionType).toObject(document.getId(), document.getData()));
                }

                futureResponse.complete(result);
            } else {
                futureResponse.completeExceptionally(task.getException());
            }
        });

        return futureResponse;
    }

    @Override
    public <T, R> CompletableFuture<List<R>> whereEqualTo(String collectionPath, String field, T value, Class<R> collectionType) {
        CompletableFuture<List<R>> futureResponse = new CompletableFuture<>();

        this.db.collection(collectionPath).whereEqualTo(field, value).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<R> result = new ArrayList<>();
                for(DocumentSnapshot document : task.getResult().getDocuments()){
                    result.add((R) registry.get(collectionType).toObject(document.getId(), document.getData()));
                }

                futureResponse.complete(result);
            } else {
                futureResponse.completeExceptionally(task.getException());
            }
        });

        return futureResponse;
    }

    @Override
    public <T, R> CompletableFuture<List<R>> whereIn(String collectionPath, String field, List<T> values, Class<R> collectionType) {
        CompletableFuture<List<R>> futureResponse = new CompletableFuture<>();

        if(values == null) {
            futureResponse.completeExceptionally(new IllegalArgumentException());
            return futureResponse;
        } else if(values.isEmpty()){
            futureResponse.complete(new ArrayList<>());
            return futureResponse;
        }
        
        List<CompletableFuture<List<R>>> futures = values
                .stream()
                .map(value -> this.whereEqualTo(collectionPath, field, value, collectionType))
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

    @Override
    public <T> CompletableFuture<List<T>> geoQuery(LatLng location, double radius, String collectionPath, Class<T> collectionType){
        radius *= (radius < 1000) ? 1000 : 1; //Check if radius is still in km, convert to m

        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(new GeoLocation(location.latitude, location.longitude), radius);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds){
            Query q = db.collection(collectionPath)
                    .orderBy(GEOHASH_KEY)
                    .startAt(b.startHash)
                    .endAt(b.endHash);
            tasks.add(q.get());
        }

        CompletableFuture<List<T>> futureResponse = new CompletableFuture<>();

        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();
                        List<T> result = new ArrayList<>();
                        for (Task<QuerySnapshot> t : tasks) {
                            QuerySnapshot snap = t.getResult();
                            matchingDocs.addAll(snap.getDocuments());
                        }
                        for (DocumentSnapshot doc : matchingDocs){
                            result.add((T) registry.get(collectionType).toObject(doc.getId(), doc.getData()));
                        }
                        futureResponse.complete(result);
                    } else {
                        futureResponse.completeExceptionally(task.getException());
                    }
                });

        return futureResponse;
    }
}

