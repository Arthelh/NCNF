package com.ncnf.repositories;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class OrganizationRepositoryTests {

    private static final FirebaseDatabase firebaseDatabase = Mockito.mock(FirebaseDatabase.class);
    Event event1 = new Event("ownerId1", "name1", LocalDateTime.now(),  new GeoPoint(0., 0.), "address", "description", Event.Type.OTHER, 0, 0, "test@email.com");
    private final Organization o1 = new Organization("EPFL", new GeoPoint(1,1), "Ecublens", "epfl@bar.com", "08008008080", "Vetterli");
    private final List<Organization> organizations = Collections.singletonList(o1);

    @Test
    public void loadOrganizationWorks(){
        when(firebaseDatabase.getDocument(anyString(), eq(Organization.class))).thenReturn(CompletableFuture.completedFuture(organizations.get(0)));

        OrganizationRepository organizationRepository = new OrganizationRepository(firebaseDatabase);
        CompletableFuture<Organization> organization = organizationRepository.loadOrganization(o1.getUuid().toString());

        try {
            assertThat(organization.get(), is(organizations.get(0)));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void storeOrganizationWorks(){
        when(firebaseDatabase.setDocument(anyString(), any())).thenReturn(CompletableFuture.completedFuture(true));

        OrganizationRepository organizationRepository = new OrganizationRepository(firebaseDatabase);
        CompletableFuture<Boolean> boolFuture = organizationRepository.storeOrganization(o1);
        try {
            assertEquals(boolFuture.get(), true);
        } catch (Exception e) {
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getUserOrganizationsIsSuccessful() {
        when(firebaseDatabase.whereArrayContains(anyString(), anyString(), anyString(), eq(Organization.class)))
                .thenReturn(CompletableFuture.completedFuture(organizations));

        OrganizationRepository repository = new OrganizationRepository(firebaseDatabase);
        CompletableFuture<List<Organization>> future = repository.getUserOrganizations("u1");

        try {
            assertEquals(future.get(), organizations);
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void addUserToOrganizationIsSuccessful() {
        when(firebaseDatabase.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        OrganizationRepository repository = new OrganizationRepository(firebaseDatabase);
        CompletableFuture<Boolean> future = repository.addUserToOrganization("u1", "o1");

        try {
            assertTrue(future.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void addUserToOrganizationFails() {
        when(firebaseDatabase.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(false));

        OrganizationRepository repository = new OrganizationRepository(firebaseDatabase);
        CompletableFuture<Boolean> future = repository.addUserToOrganization("u1", "o1");

        try {
            assertFalse(future.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void addEventToOrganizationWorks(){
        when(firebaseDatabase.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        OrganizationRepository repository = new OrganizationRepository(firebaseDatabase);
        CompletableFuture<Boolean> future = repository.addEventToOrganization(o1.getUuid().toString(), event1.getUuid().toString());

        try {
            assertTrue(future.get());
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getByUuidWorks(){
        when(firebaseDatabase.whereEqualTo(anyString(), anyString(), anyString(), eq(Organization.class))).thenReturn(CompletableFuture.completedFuture(organizations));

        OrganizationRepository organizationRepository = new OrganizationRepository(firebaseDatabase);
        CompletableFuture<List<Organization>> organization = organizationRepository.getByUUID(o1.getUuid().toString());

        try {
            assertThat(organization.get(), is(organizations));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getOrganizationEventsWorks(){
        when(firebaseDatabase.whereEqualTo(anyString(), anyString(), anyString(), eq(Event.class))).thenReturn(CompletableFuture.completedFuture(Arrays.asList(event1)));

        OrganizationRepository organizationRepository = new OrganizationRepository(firebaseDatabase);
        CompletableFuture<List<Event>> events = organizationRepository.getOrganizationEvents(o1.getUuid().toString());

        try {
            assertThat(events.get(), is(Arrays.asList(event1)));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getOrganizationWithTokenWorks(){
        when(firebaseDatabase.withFieldContaining(anyString(), anyString(), anyString(), eq(Organization.class))).thenReturn(CompletableFuture.completedFuture(organizations));

        OrganizationRepository organizationRepository = new OrganizationRepository(firebaseDatabase);
        CompletableFuture<List<Organization>> organization = organizationRepository.getOrganizationsWithToken("token");

        try {
            assertThat(organization.get(), is(organizations));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void getByNameIsSuccessful() {
        when(firebaseDatabase.withFieldContaining(anyString(), anyString(), anyString(), eq(Organization.class)))
                .thenReturn(CompletableFuture.completedFuture(organizations));

        OrganizationRepository repository = new OrganizationRepository(firebaseDatabase);
        CompletableFuture<List<Organization>> future = repository.getByName("EPFL");

        try {
            assertEquals(future.get(), organizations);
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

}
