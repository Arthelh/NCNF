package com.ncnf.repositories;

import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Group;
import com.ncnf.models.Organization;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.utilities.StringCodes.*;


public class OrganizationRepository {

    private final FirebaseDatabase db;

    public OrganizationRepository() {
        this.db = new FirebaseDatabase();
    }

    @Inject
    public OrganizationRepository(FirebaseDatabase db)  {
        this.db = db;
    }

    /**
     * Loads the Organization from Database
     * @param uuid the unique identifier of the Organization
     * @return A CompletableFuture wrapping the loaded Organization
     */
    public CompletableFuture<Organization> loadOrganization(String uuid){
        return this.db.getDocument(ORGANIZATIONS_COLLECTION_KEY + uuid, Organization.class);
    }

    /**
     * Stores the Organization to Database
     * @param organization the Organization object to store
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    public CompletableFuture<Boolean> storeOrganization(Organization organization){
        return this.db.setDocument(ORGANIZATIONS_COLLECTION_KEY + organization.getUuid(), organization);
    }

    /**
     * Loads multiple Organization objects from Database
     * @param uuidList the list of Organization unique identifiers to load
     * @return A CompletableFuture wrapping a list containing the loaded Organization objects
     */
    public CompletableFuture<List<Organization>> loadMultipleOrganizations(List<String> uuidList){
        return this.db.whereIn(ORGANIZATIONS_COLLECTION_KEY, UUID_KEY, uuidList, Organization.class);
    }

    /**
     * Get the organization a user belongs to
     * @param uuid the user identifier
     * @return a future organization
     */
    public CompletableFuture<List<Organization>> getUserOrganizations(String uuid) {
        return db.whereArrayContains(ORGANIZATIONS_COLLECTION_KEY, ADMIN_KEY, uuid, Organization.class);
    }

    /**
     * Add a new admin to an organization
     * @param user_id a user identifier
     * @param organization_id an organization identifier
     * @return whether the update succeed
     */
    public CompletableFuture<Boolean> addUserToOrganization(String user_id, String organization_id) {
        CompletableFuture<Boolean> r1 =
                db.updateArrayField(ORGANIZATIONS_COLLECTION_KEY + organization_id, ADMIN_KEY, user_id);
        CompletableFuture<Boolean> r2 =
                db.updateArrayField(USERS_COLLECTION_KEY + user_id, USER_ORGANIZATIONS, organization_id);

        return combine(r1, r2);
    }

    /**
     * Fetch an organization given its name
     * @param name name of the organization
     * @return an organization
     */
    public CompletableFuture<List<Organization>> getByName(String name){
        return db.withFieldContaining(ORGANIZATIONS_COLLECTION_KEY, ORGANIZATION_NAME, name, Organization.class);
    }


    public CompletableFuture<List<Organization>> getOrganizationsWithToken(String token){
        return db.withFieldContaining(ORGANIZATIONS_COLLECTION_KEY, ORGANIZATION_ADMIN_TOKEN, token, Organization.class);
    }

    private CompletableFuture<Boolean> combine(CompletableFuture<Boolean> u1, CompletableFuture<Boolean> u2) {
        return u1.thenCombine(u2, (v1, v2) -> v1 && v2)
                .exceptionally(exception -> false);
    }
}
