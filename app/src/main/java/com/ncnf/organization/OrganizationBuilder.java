package com.ncnf.organization;

import android.util.Log;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.ADDRESS_KEY;
import static com.ncnf.Utils.ADMIN_KEY;
import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.EVENTS_COLLECTION_KEY;
import static com.ncnf.Utils.LOCATION_KEY;
import static com.ncnf.Utils.NAME_KEY;
import static com.ncnf.Utils.ORGANIZATIONS_COLLECTION_KEY;
import static com.ncnf.Utils.PHONE_NB_KEY;
import static com.ncnf.Utils.UUID_KEY;

public class OrganizationBuilder {


    private DatabaseService db;

    public OrganizationBuilder(){
        this.db = new DatabaseService();
    }

    public OrganizationBuilder(DatabaseService db){
        if(db == null){
            throw new IllegalArgumentException("Database is null");
        }
        this.db = db;
    }

    public CompletableFuture<DatabaseResponse> loadFromDB(String uuid){
        CompletableFuture<DatabaseResponse> getData = db.getData(ORGANIZATIONS_COLLECTION_KEY + uuid);
        return getData.thenApply(task -> {
            if(task.isSuccessful()){
                try {
                    Map<String, Object> data = (Map<String, Object>) task.getResult();
                    Organization organization = build(data);
                    return new DatabaseResponse(true, organization, null);


                } catch (Exception e){
                    return new DatabaseResponse(false, null, e);
                }
            }
            return new DatabaseResponse(false, null, task.getException());
        });
    }

    public Organization build(Map<String, Object> data){
        try {
            String uuidStr = data.get(UUID_KEY).toString();
            String name = data.get(NAME_KEY).toString();
            String address = data.get(ADDRESS_KEY).toString();
            GeoPoint location = (GeoPoint) data.get(LOCATION_KEY);
            String phoneNb = data.get(PHONE_NB_KEY).toString();
            List<String> admin = (List<String>) data.get(ADMIN_KEY);
            List<String> events = (List<String>) data.get(EVENTS_COLLECTION_KEY);

            return new Organization(UUID.fromString(uuidStr), name, location, address, phoneNb, admin, events);
        } catch (Exception e){
            Log.d(DEBUG_TAG, "Couldn't load organization");
            return null;
        }
    }
}
