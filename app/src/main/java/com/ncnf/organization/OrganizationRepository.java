package com.ncnf.organization;

import com.ncnf.database.DatabaseService;
import com.ncnf.utilities.StringCodes;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.utilities.StringCodes.*;


public class OrganizationRepository {

    private final DatabaseService db;

    public OrganizationRepository() {
        this.db = new DatabaseService();
    }

    @Inject
    public OrganizationRepository(DatabaseService db)  {
        this.db = db;
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
