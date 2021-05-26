package com.ncnf.repositories;

import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Group;
import com.ncnf.models.Organization;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.utilities.Helpers.combine;
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
     * Get all the organizations where the user is an administrator
     * @param uuid User's identifier
     * @return CompletableFuture containing a list of organizations
     */
    public CompletableFuture<List<Organization>> getUserOrganizations(String uuid) {
        return db.whereArrayContains(ORGANIZATIONS_COLLECTION_KEY, ADMIN_KEY, uuid, Organization.class);
    }

    /**
     * Add a new admin to an organization
     * @param user_id User's identifier
     * @param organization_id Organization's identifier
     * @return CompletableFuture containing the Firebase's response : true if all the operations succeeded
     */
    public CompletableFuture<Boolean> addUserToOrganization(String user_id, String organization_id) {
        CompletableFuture<Boolean> r1 =
                db.updateArrayField(ORGANIZATIONS_COLLECTION_KEY + organization_id, ADMIN_KEY, user_id);
        CompletableFuture<Boolean> r2 =
                db.updateArrayField(USERS_COLLECTION_KEY + user_id, USER_ORGANIZATIONS, organization_id);

        return combine(r1, r2);
    }

    /**
     * Retrieve all organizations whose name is equal to the given one
     * @param name Name we want to match
     * @return CompletableFuture containing a list of organizations that fulfills the condition
     */
    public CompletableFuture<List<Organization>> getByName(String name){
        return db.withFieldContaining(ORGANIZATIONS_COLLECTION_KEY, ORGANIZATION_NAME, name, Organization.class);
    }

    /**
     * Retrieve all organizations whose token is equal to the given one
     * @param token Token we want to match
     * @return CompletableFuture containing a list of organizations that fulfills the condition
     */
    public CompletableFuture<List<Organization>> getOrganizationsWithToken(String token){
        return db.withFieldContaining(ORGANIZATIONS_COLLECTION_KEY, ORGANIZATION_ADMIN_TOKEN, token, Organization.class);
    }
}
