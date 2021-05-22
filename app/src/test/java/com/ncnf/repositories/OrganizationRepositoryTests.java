package com.ncnf.repositories;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Organization;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class OrganizationRepositoryTests {

    private static FirebaseDatabase firebaseDatabase = Mockito.mock(FirebaseDatabase.class);

    private final Organization o1 = new Organization("EPFL", new GeoPoint(1,1), "Ecublens", "epfl@bar.com", "08008008080", "Vetterli");
    private final List<Organization> organizations = Collections.singletonList(o1);

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
