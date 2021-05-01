package com.ncnf.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ncnf.database.builder.EventBuilder;
import com.ncnf.event.Event;
import com.ncnf.event.Social;
import com.ncnf.mocks.MockTask;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.ncnf.Utils.NAME_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class DatabaseServiceTest {

    private FirebaseFirestore db;
    private static DatabaseService service;
    private static Social social;
    private static Task task;

    String name = "Jane Doe";
    Date date = new Date(2021, 03, 11);
    GeoPoint geoPoint = new GeoPoint(0., 0.);
    String address = "north pole";
    Social.Type type = Social.Type.Conference;
    String description = "Social description goes here";
    String ownerID = "00";

    @Before
    public void setup() {
        db = Mockito.mock(FirebaseFirestore.class, Mockito.RETURNS_DEEP_STUBS);
        service = new DatabaseService(db);

        social = new Event(ownerID, name, date, geoPoint,address,description, type, 0, 0, "test@email.com");
        task = new MockTask<Social>(social, null);
    }

    @Test
    public void getFieldWorks(){
        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        String name = "name";
        when(document.get(anyString())).thenReturn(name);
        task = new MockTask(document, null);
        when(db.document(anyString()).get()).thenReturn(task);

        CompletableFuture<String> future = service.getField("path", NAME_KEY);

        try{
            assertEquals(future.get(), name);
        } catch (Exception e){
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getFieldFails(){
        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        String name = "name";
        when(document.get(anyString())).thenReturn(name);
        task = new MockTask(document, new IllegalArgumentException(), false);
        when(db.document(anyString()).get()).thenReturn(task);

        CompletableFuture<String> future = service.getField("path", NAME_KEY);

        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void updateFieldWorks(){
        when(db.document(anyString()).update((FieldPath) anyObject(), anyObject())).thenReturn(task);

        CompletableFuture<Boolean> future = service.updateField("path", NAME_KEY, name);

        try{
            assertTrue(future.get());
        } catch (Exception e){
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void updateFieldFails(){
        task = new MockTask(social, new IllegalArgumentException(), false);
        when(db.document(anyString()).update((FieldPath) anyObject(), anyObject())).thenReturn(task);

        CompletableFuture<Boolean> future = service.updateField("path", NAME_KEY, name);

        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void getDocumentWorks(){
        Event event = new Event(UUID.randomUUID().toString(), name, date, geoPoint,address,description, type, 0, 0, "test@email.com");
        Map<String, Object> data = new EventBuilder().toMap(event);

        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        when(document.getData()).thenReturn(data);
        task = new MockTask(document, null, true);
        when(db.document(anyString()).get()).thenReturn(task);

        CompletableFuture<Event> future = service.getDocument("path", Event.class);

        try{
            assertEquals(future.get(), event);
        } catch (Exception e){
            Assert.fail("The future did not complete correctly !");
        }

    }

    @Test
    public void getDocumentFails(){
        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        String name = "name";
        when(document.get(anyString())).thenReturn(name);
        task = new MockTask(document, new IllegalArgumentException(), false);
        when(db.document(anyString()).get()).thenReturn(task);

        CompletableFuture<Social> future = service.getDocument("path", Social.class);

        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void setDocumentWorks() {
        when(db.document(anyString()).set(anyObject())).thenReturn(task);

        CompletableFuture<Boolean> future = service.setDocument("/events", social);

        try {
            assertTrue(future.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void setDocumentFails(){
        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        task = new MockTask(document, new IllegalArgumentException(), false);
        when(db.document(anyString()).set(anyObject())).thenReturn(task);

        CompletableFuture<Boolean> future = service.setDocument("/events", social);

        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void updatingArrayFieldWorks(){
        when(db.document(anyString()).update((FieldPath) anyObject(), anyObject())).thenReturn(task);

        CompletableFuture<Boolean> future = service.updateArrayField("/events", NAME_KEY, "Conference");

        try {
            assertTrue(future.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void updatingArrayFieldFails(){
        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        task = new MockTask(document, new IllegalArgumentException(), false);
        when(db.document(anyString()).update((FieldPath) anyObject(), anyObject())).thenReturn(task);

        CompletableFuture<Boolean> future = service.updateArrayField("/events", NAME_KEY, "Conference");

        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void removingArrayFieldWorks(){
        when(db.document(anyString()).update((FieldPath) anyObject(), anyObject())).thenReturn(task);

        CompletableFuture<Boolean> future = service.removeArrayField("/events", NAME_KEY, "Conference");

        try {
            assertTrue(future.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void removingArrayFieldFails(){
        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        task = new MockTask(document, new IllegalArgumentException(), false);
        when(db.document(anyString()).update((FieldPath) anyObject(), anyObject())).thenReturn(task);

        CompletableFuture<Boolean> future = service.removeArrayField("/events", NAME_KEY, "Conference");

        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void getCollectionWorks(){

        String uuid = UUID.randomUUID().toString();
        Event event = new Event(uuid, name, date, geoPoint,address,description, type, 0, 0, "test@email.com");
        Map<String, Object> data = new EventBuilder().toMap(event);

        QueryDocumentSnapshot document = Mockito.mock(QueryDocumentSnapshot.class);
        when(document.getData()).thenReturn(data);
        when(document.getId()).thenReturn(uuid);

        QuerySnapshot query = Mockito.mock(QuerySnapshot.class);
        when(query.getDocuments()).thenReturn(Arrays.asList(document));
        Task<QuerySnapshot> task = new MockTask(query, null, true);

        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        when(db.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.get()).thenReturn(task);

       CompletableFuture<List<Social>> res = service.getCollection("/events", Social.class);

        try {
            assertEquals(event, res.get().get(0));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getCollectionFails(){
        Task<QuerySnapshot> task = new MockTask(null, new IllegalArgumentException(), false);

        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        when(db.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.get()).thenReturn(task);

        CompletableFuture<List<Social>> future = service.getCollection("/events", Social.class);

        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void withFieldLikeWorks(){
        String uuid = UUID.randomUUID().toString();
        Event event = new Event(uuid, name, date, geoPoint,address,description, type, 0, 0, "test@email.com");
        Map<String, Object> data = new EventBuilder().toMap(event);

        QueryDocumentSnapshot document = Mockito.mock(QueryDocumentSnapshot.class);
        when(document.getData()).thenReturn(data);
        when(document.getId()).thenReturn(uuid);

        QuerySnapshot querySnapshot = Mockito.mock(QuerySnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(document));
        Task<QuerySnapshot> task = new MockTask(querySnapshot, null, true);

        Query query = Mockito.mock(Query.class);
        when(query.startAt(anyString())).thenReturn(query);
        when(query.endAt(anyString())).thenReturn(query);
        when(query.get()).thenReturn(task);


        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        when(db.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.orderBy(anyString())).thenReturn(query);

        CompletableFuture<List<Social>> future = service.withFieldLike("/events", "field", "value", Social.class);

        try {
            assertEquals(event, future.get().get(0));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void withFieldLikeFails(){
        Task<QuerySnapshot> task = new MockTask(null, new IllegalArgumentException(), false);

        Query query = Mockito.mock(Query.class);
        when(query.startAt(anyString())).thenReturn(query);
        when(query.endAt(anyString())).thenReturn(query);
        when(query.get()).thenReturn(task);

        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        when(db.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.orderBy(anyString())).thenReturn(query);

        CompletableFuture<List<Social>> future = service.withFieldLike("/events", "field", "value", Social.class);

        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void whereArrayContains(){
        String uuid = UUID.randomUUID().toString();
        Event event = new Event(uuid, name, date, geoPoint,address,description, type, 0, 0, "test@email.com");
        Map<String, Object> data = new EventBuilder().toMap(event);

        QueryDocumentSnapshot document = Mockito.mock(QueryDocumentSnapshot.class);
        when(document.getData()).thenReturn(data);
        when(document.getId()).thenReturn(uuid);

        QuerySnapshot querySnapshot = Mockito.mock(QuerySnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(document));
        Task<QuerySnapshot> task = new MockTask(querySnapshot, null, true);

        Query query = Mockito.mock(Query.class);
        when(query.get()).thenReturn(task);


        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        when(db.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.whereArrayContains(anyString(), anyString())).thenReturn(query);

        CompletableFuture<List<Social>> future = service.whereArrayContains("/events", "field", "value", Social.class);

        try {
            assertEquals(event, future.get().get(0));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void whereArrayContainsFails(){
        Task<QuerySnapshot> task = new MockTask(null, new IllegalArgumentException(), false);

        Query query = Mockito.mock(Query.class);
        when(query.get()).thenReturn(task);

        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        when(db.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.whereArrayContains(anyString(), anyString())).thenReturn(query);

        CompletableFuture<List<Social>> future = service.whereArrayContains("/events", "field", "value", Social.class);

        assertTrue(future.isCompletedExceptionally());
    }

    @Test
    public void whereEqualTo(){
        String uuid = UUID.randomUUID().toString();
        Event event = new Event(uuid, name, date, geoPoint,address,description, type, 0, 0L, "test@email.com");
        Map<String, Object> data = new EventBuilder().toMap(event);

        QueryDocumentSnapshot document = Mockito.mock(QueryDocumentSnapshot.class);
        when(document.getData()).thenReturn(data);
        when(document.getId()).thenReturn(uuid);

        QuerySnapshot querySnapshot = Mockito.mock(QuerySnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(Arrays.asList(document));
        Task<QuerySnapshot> task = new MockTask(querySnapshot, null, true);

        Query query = Mockito.mock(Query.class);
        when(query.get()).thenReturn(task);


        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        when(db.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.whereEqualTo(anyString(), anyString())).thenReturn(query);

        CompletableFuture<List<Social>> future = service.whereEqualTo("/events", "field", "value", Social.class);

        try {
            assertEquals(event, future.get().get(0));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void whereEqualToFails(){
        Task<QuerySnapshot> task = new MockTask(null, new IllegalArgumentException(), false);

        Query query = Mockito.mock(Query.class);
        when(query.get()).thenReturn(task);

        CollectionReference mockCollection = Mockito.mock(CollectionReference.class);
        when(db.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.whereEqualTo(anyString(), anyString())).thenReturn(query);

        CompletableFuture<List<Social>> future = service.whereEqualTo("/events", "field", "value", Social.class);

        assertTrue(future.isCompletedExceptionally());
    }

    /*
    @Test
    public void whereInWorks(){
        CompletableFuture future = CompletableFuture.completedFuture(Arrays.asList(social));
        when(service.whereEqualTo(anyString(), anyString(), any(), any())).thenReturn(future);

        CompletableFuture<List<Social>> listFuture = service.whereIn("/events", "field", Arrays.asList("value"), Social.class);

        try {
            assertEquals(listFuture.get().get(0), social);
        } catch (Exception e){
            Assert.fail("The future did not complete correctly !");
        }
    }
    */

    @Test
    public void whereInFailsOnEmpty(){
        CompletableFuture<List<Social>> future = service.whereIn("path", "path", null, Social.class);
        assertTrue(future.isCompletedExceptionally());
        future = service.whereIn("path", "path", new ArrayList<>(), Social.class);

        try {
            assertTrue(future.get().isEmpty());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void deleteDocumentWorks() {
        when(db.document(anyString()).delete()).thenReturn(task);

        CompletableFuture<Boolean> future = service.deleteDocument("/events");

        try {
            assertTrue(future.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void deleteDocumentFails(){
        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        task = new MockTask(document, new IllegalArgumentException(), false);
        when(db.document(anyString()).delete()).thenReturn(task);

        CompletableFuture<Boolean> future = service.deleteDocument("/events");

        assertTrue(future.isCompletedExceptionally());
    }
}
