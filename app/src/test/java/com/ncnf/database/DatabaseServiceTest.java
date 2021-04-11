package com.ncnf.database;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.event.Event;
import com.ncnf.event.PublicEvent;
import com.ncnf.mocks.MockTask;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.ncnf.Utils.EVENTs_COLLECTION_KEY;
import static com.ncnf.Utils.NAME_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class DatabaseServiceTest {

    private FirebaseFirestore db;
    private static DatabaseService service;
    private static Event event;
    private static Task task;

    @Before
    public void setup() {
        db = Mockito.mock(FirebaseFirestore.class, Mockito.RETURNS_DEEP_STUBS);
        service = new DatabaseService(db);
        event = new PublicEvent("00","testName", new Date(), new GeoPoint(0., 0.),"north pole","testData", Event.Type.Museum, 0 , 0);
        task = new MockTask<Event>(event, null);
    }

    @Test
    public void setDocumentReturnResult() {
        when(db.document(anyString()).set(anyObject())).thenReturn(task);

        CompletableFuture<DatabaseResponse> future = service.setDocument("/events", new HashMap<>());

        try {
            assertEquals(event, future.get().getResult());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void updateFieldReturnResult() {
        when(db.document(anyString()).update((FieldPath) anyObject(), anyObject())).thenReturn(task);

        CompletableFuture<DatabaseResponse> future = service.updateField("/events", NAME_KEY, "Conference");

        try {
            assertEquals(event, future.get().getResult());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getDataReturnResult() {
        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        Map<String, Object> data = new HashMap<>();
        when(document.getData()).thenReturn(data);
        task = new MockTask(document, null);

        when(db.document(anyString()).get()).thenReturn(task);

        CompletableFuture<DatabaseResponse> future = service.getData("/events");

        try {
            assertEquals(data, future.get().getResult());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getFieldWorks(){
        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        Map<String, Object> map = new HashMap<>();
        String name = "name";
        map.put(NAME_KEY, name);
        when(document.getData()).thenReturn(map);
        task = new MockTask(document, null);
        when(db.document(anyString()).get()).thenReturn(task);
        CompletableFuture<DatabaseResponse> future = service.getField(EVENTs_COLLECTION_KEY, NAME_KEY);

        try {
            assertEquals(name, future.get().getResult());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getFieldFails(){
        DocumentSnapshot document = Mockito.mock(DocumentSnapshot.class);
        Map<String, Object> map = new HashMap<>();
        String name = "name";
        map.put(NAME_KEY, name);
        when(document.getData()).thenReturn(map);
        task = new MockTask(null, new IllegalArgumentException(), false);
        when(db.document(anyString()).get()).thenReturn(task);
        CompletableFuture<DatabaseResponse> future = service.getField(EVENTs_COLLECTION_KEY, NAME_KEY);

        try {
            DatabaseResponse response = future.get();
            assertTrue(!response.isSuccessful());
            assertTrue(response.getException() instanceof IllegalArgumentException);
            assertTrue(response.getResult() == null);
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void updatingArrayFieldWorks(){
        when(db.document(anyString()).update((FieldPath) anyObject(), anyObject())).thenReturn(task);

        CompletableFuture<DatabaseResponse> future = service.updateArrayField("/events", NAME_KEY, "Conference");

        try {
            assertEquals(event, future.get().getResult());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void deleteReturnResult() {
        when(db.document(anyString()).delete()).thenReturn(task);

        CompletableFuture<DatabaseResponse> future = service.delete("/events");

        try {
            assertEquals(event, future.get().getResult());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }
}
