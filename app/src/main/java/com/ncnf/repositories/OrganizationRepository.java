package com.ncnf.repositories;

import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;
import com.ncnf.utilities.settings.Settings;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.utilities.StringCodes.ADMIN_KEY;
import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.ORGANIZATIONS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.ORGANIZATION_ADMIN_TOKEN;
import static com.ncnf.utilities.StringCodes.ORGANIZATION_NAME;
import static com.ncnf.utilities.StringCodes.ORGANIZATION_UUID;
import static com.ncnf.utilities.StringCodes.ORGANIZED_EVENTS;
import static com.ncnf.utilities.StringCodes.OWNER_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.USER_ORGANIZATIONS;


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
        return this.db.whereIn(ORGANIZATIONS_COLLECTION_KEY, ORGANIZATION_UUID, uuidList, Organization.class);
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

    public CompletableFuture<Boolean> addEventToOrganization(String organization_id, String event_id){
        return db.updateArrayField(ORGANIZATIONS_COLLECTION_KEY + organization_id, ORGANIZED_EVENTS, event_id);
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
     * Retrieve all organizations whose uuid is equal to the given one
     * @param organization_id Identifier we want to match
     * @return CompletableFuture containing a list of organizations that fulfills the condition
     */
    public CompletableFuture<List<Organization>> getByUUID(String organization_id){
        return db.whereEqualTo(ORGANIZATIONS_COLLECTION_KEY, ORGANIZATION_UUID, organization_id, Organization.class);
    }

    /**
     * Retrieve all events organized by the organization with thr given ID
     * @param organization_id Organization whose events are wanted
     * @return CompletableFuture containing a list of the wanted events
     */
    public CompletableFuture<List<Event>> getOrganizationEvents(String organization_id) {
        return db.whereEqualTo(EVENTS_COLLECTION_KEY, OWNER_KEY, organization_id, Event.class);
    }


    /**
     * Retrieve all organizations whose token is equal to the given one
     * @param token Token we want to match
     * @return CompletableFuture containing a list of organizations that fulfills the condition
     */
    public CompletableFuture<List<Organization>> getOrganizationsWithToken(String token){
        return db.withFieldContaining(ORGANIZATIONS_COLLECTION_KEY, ORGANIZATION_ADMIN_TOKEN, token, Organization.class);
    }

    private CompletableFuture<Boolean> combine(CompletableFuture<Boolean> u1, CompletableFuture<Boolean> u2) {
        return u1.thenCombine(u2, (v1, v2) -> v1 && v2)
                .exceptionally(exception -> false);
    }

    /**
     * Returns all organization that are in a radius set in the settings around the current user position
     * @return A CompletableFuture wrapping a list containing the nearby Organization objects
     */
    public CompletableFuture<List<Organization>> getOrganizationsNearby(){
        return db.geoQuery(Settings.getUserPosition(), Settings.getCurrentMaxDistance() * 1000, ORGANIZATIONS_COLLECTION_KEY, Organization.class);
    }
}
